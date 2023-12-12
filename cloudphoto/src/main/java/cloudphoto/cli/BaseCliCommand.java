package cloudphoto.cli;

import cloudphoto.common.*;
import cloudphoto.common.errorresult.*;

public abstract class BaseCliCommand<Executor extends CliCommandExecutor> implements CliCommand {
	private final Lazy<Executor> commandExecutor;

	public BaseCliCommand(Lazy<Executor> commandExecutor) {
		this.commandExecutor = commandExecutor;
	}

	@Override
	public Result<String> call() throws Exception {
		return commandExecutor.get().execute(this);
	}
}
