package cloudphoto.cli.commands.delete;

import cloudphoto.albums.*;
import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;

import java.util.function.*;

public class DeleteCommandExecutor implements CliCommandExecutor<DeleteCommand> {
	private final Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>>
		s3SettingsProvider;
	private final AlbumService albumService;

	public DeleteCommandExecutor(
			Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService) {
		this.s3SettingsProvider = s3SettingsProvider;
		this.albumService = albumService;
	}

	@Override
	public Result<String> execute(DeleteCommand deleteCommand) {
		var s3SettingsResult = s3SettingsProvider.get();
		if (s3SettingsResult.isFailure())
			return Result.fail(s3SettingsResult.getError());

		if (deleteCommand.photoFileName == null)
			throw new UnsupportedOperationException();

		return albumService.deletePhoto(deleteCommand.albumName, deleteCommand.photoFileName);
	}
}
