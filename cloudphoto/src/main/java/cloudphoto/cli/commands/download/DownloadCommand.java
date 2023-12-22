package cloudphoto.cli.commands.download;

import cloudphoto.cli.*;
import cloudphoto.common.*;
import picocli.CommandLine.*;

@Command(name = "download")
public class DownloadCommand extends BaseCliCommand<DownloadCommandExecutor> {
	@Option(names = "--album", required = true, description = "album name")
	public String albumName;

	@Option(names = "--path", description = "album name")
	public String photosLocalPath;

	public DownloadCommand(Lazy<DownloadCommandExecutor> commandExecutor) {
		super(commandExecutor);
	}
}
