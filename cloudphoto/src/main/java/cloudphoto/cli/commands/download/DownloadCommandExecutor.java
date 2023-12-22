package cloudphoto.cli.commands.download;

import cloudphoto.albums.*;
import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;

import java.util.function.*;

public class DownloadCommandExecutor implements CliCommandExecutor<DownloadCommand> {
	private final Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>>
		s3SettingsProvider;
	private final AlbumService albumService;

	public DownloadCommandExecutor(
			Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService) {
		this.s3SettingsProvider = s3SettingsProvider;
		this.albumService = albumService;
	}

	@Override
	public Result<String> execute(DownloadCommand downloadCommand) {
		var s3SettingsResult = s3SettingsProvider.get();
		if (s3SettingsResult.isFailure())
			return Result.fail(s3SettingsResult.getError());

		var photosLocalPath = downloadCommand.photosLocalPath != null
			? downloadCommand.photosLocalPath
			: System.getProperty("user.dir");

		return albumService.downloadPhotos(downloadCommand.albumName, photosLocalPath);
	}
}
