package cloudphoto;

import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.*;
import picocli.*;
import picocli.CommandLine.*;

import java.util.*;
import java.util.stream.*;

@Command(name = "cloudphoto")
public class Application {
	private static final int errorExitCode = 1;

	private final List<CliCommand> cliCommandExecutors;

	public Application(List<CliCommand> cliCommandExecutors) {
		this.cliCommandExecutors = cliCommandExecutors;
	}

	public cloudphoto.common.result.Result run(String[] args) {
		var cli = initializeCli();

		cli.execute(args);

		var commandExecutionResults = getNotNullCommandExecutionResults(cli);
		if (commandExecutionResults.isEmpty()) // Attempt to execute unknown command
			return cloudphoto.common.result.Result.fail; // Error was already reported by picocli library

		var isSuccess = true;
		for (var commandExecutionResult : commandExecutionResults)
			if (commandExecutionResult.isFailure()) {
				isSuccess = false;
				System.err.println(commandExecutionResult.getError());
			}
		return cloudphoto.common.result.Result.create(isSuccess);
	}

	private CommandLine initializeCli() {
		var cli = new CommandLine(this);
		for (var cliCommandExecutor : cliCommandExecutors)
			cli.addSubcommand(cliCommandExecutor);
		return cli;
	}

	public static void main(String[] args) {
		try {
			var diContainer = ApplicationConfigurator.createDiContainer();
			var application = diContainer.getBean(Application.class);

			var applicationRunResult = application.run(args);
			if (applicationRunResult.isFailure())
				System.exit(errorExitCode);
		}
		catch (Throwable throwable) {
			System.err.print("Unexpected error: ");
			throwable.printStackTrace(System.err);
			System.exit(errorExitCode);
		}
	}

	private static List<Result<String>> getNotNullCommandExecutionResults(CommandLine cli) {
		return cli.getSubcommands()
			.values().stream()
			.map(CommandLine::<Result<String>>getExecutionResult)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}
}
