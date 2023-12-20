package cloudphoto.cli.commands.list;

import cloudphoto.albums.*;
import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;

import java.util.function.*;

public class ListCommandExecutor implements CliCommandExecutor<ListCommand> {
	private final Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>>
		s3SettingsProvider;
	private final AlbumService albumService;
	private final Console console;

	public ListCommandExecutor(
			Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService,
			Console console) {
		this.s3SettingsProvider = s3SettingsProvider;
		this.albumService = albumService;
		this.console = console;
	}

	@Override
	public Result<String> execute(ListCommand listCommand) {
		var s3SettingsResult = s3SettingsProvider.get();
		if (s3SettingsResult.isFailure())
			return Result.fail(s3SettingsResult.getError());

		return listCommand.albumName != null
			? listPhotos(listCommand.albumName)
			: listAlbums();
	}

	private Result<String> listAlbums() {
		var albumNamesResult = albumService.listAlbums();
		if (albumNamesResult.isFailure())
			return Result.fail(albumNamesResult.getError());

		console.output(albumNamesResult.getValue());
		return Result.success();
	}

	private Result<String> listPhotos(String albumName) {
		var photoFileNamesResult = albumService.listPhotos(albumName);
		if (photoFileNamesResult.isFailure())
			return Result.fail(photoFileNamesResult.getError());

		console.output(photoFileNamesResult.getValue());
		return Result.success();
	}
}
