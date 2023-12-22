package cloudphoto.cli.commands.mksite;

import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.site.*;

import java.util.function.*;

public class MkSiteCommandExecutor implements CliCommandExecutor<MkSiteCommand> {
	private final Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>>
		s3SettingsProvider;
	private final SiteService siteService;
	private final Console console;

	public MkSiteCommandExecutor(
			Supplier<cloudphoto.common.valueerrorresult.Result<S3Settings, String>> s3SettingsProvider,
			SiteService siteService,
			Console console) {
		this.s3SettingsProvider = s3SettingsProvider;
		this.siteService = siteService;
		this.console = console;
	}

	@Override
	public Result<String> execute(MkSiteCommand mkSiteCommand) {
		var s3SettingsResult = s3SettingsProvider.get();
		if (s3SettingsResult.isFailure())
			return Result.fail(s3SettingsResult.getError());

		var publicationResult = siteService.publishGalleria();
		if (publicationResult.isFailure())
			return Result.fail(publicationResult.getError());

		console.output(publicationResult.getValue());
		return Result.success();
	}
}
