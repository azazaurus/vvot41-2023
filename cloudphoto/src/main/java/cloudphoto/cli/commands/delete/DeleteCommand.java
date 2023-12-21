package cloudphoto.cli.commands.delete;

import cloudphoto.cli.*;
import cloudphoto.common.*;
import picocli.CommandLine.*;

@Command(name = "delete")
public class DeleteCommand extends BaseCliCommand<DeleteCommandExecutor> {
	@Option(names = "--album", required = true, description = "album name")
	public String albumName;

	@Option(names = "--photo", description = "photo file name")
	public String photoFileName;

	public DeleteCommand(Lazy<DeleteCommandExecutor> commandExecutor) {
		super(commandExecutor);
	}
}
