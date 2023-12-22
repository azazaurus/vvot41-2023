package cloudphoto.cli.commands.upload;

import cloudphoto.cli.*;
import cloudphoto.common.*;
import picocli.CommandLine.*;

@Command(name = "upload")
public class UploadCommand extends BaseCliCommand<UploadCommandExecutor> {
	@Option(names = "--album", required = true, description = "album name")
	public String albumName;

	@Option(names = "--path", description = "album name")
	public String photosLocalPath;

	public UploadCommand(Lazy<UploadCommandExecutor> commandExecutor) {
		super(commandExecutor);
	}
}
