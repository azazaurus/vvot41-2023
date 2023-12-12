package cloudphoto.cli;

import cloudphoto.common.errorresult.*;

public interface CliCommandExecutor<Command> {
	Result<String> execute(Command command) throws Exception;
}
