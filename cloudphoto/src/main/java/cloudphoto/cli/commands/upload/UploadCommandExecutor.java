package cloudphoto.cli.commands.upload;

import cloudphoto.albums.*;
import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;

import java.util.function.*;

public class UploadCommandExecutor implements CliCommandExecutor<UploadCommand> {
	private final Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>>
		s3SettingsProvider;
	private final AlbumService albumService;

	public UploadCommandExecutor(
			Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService) {
		this.s3SettingsProvider = s3SettingsProvider;
		this.albumService = albumService;
	}

	@Override
	public Result<String> execute(UploadCommand uploadCommand) {
		var s3SettingsResult = s3SettingsProvider.get();
		if (s3SettingsResult.isFailure())
			return Result.fail(s3SettingsResult.getError());

		var photosLocalPath = uploadCommand.photosLocalPath != null
			? uploadCommand.photosLocalPath
			: System.getProperty("user.dir");

		return albumService.uploadPhotos(uploadCommand.albumName, photosLocalPath);
	}
}
