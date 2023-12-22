package cloudphoto.cli.commands.init;

import cloudphoto.cli.*;
import cloudphoto.common.*;
import picocli.CommandLine.*;

@Command(name = "init")
public class InitCommand extends BaseCliCommand<InitCommandExecutor> {
	public InitCommand(Lazy<InitCommandExecutor> commandExecutor) {
		super(commandExecutor);
	}
}
