package cloudphoto.cli;

import cloudphoto.albums.*;
import cloudphoto.cli.commands.delete.*;
import cloudphoto.cli.commands.download.*;
import cloudphoto.cli.commands.init.*;
import cloudphoto.cli.commands.list.*;
import cloudphoto.cli.commands.mksite.*;
import cloudphoto.cli.commands.upload.*;
import cloudphoto.common.Lazy;
import cloudphoto.common.valueerrorresult.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.s3.*;
import cloudphoto.site.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.*;

import java.io.*;
import java.util.function.*;

@Configuration
public class CliConfigurator {
	@Bean
	public Console console() {
		return new IoStreamConsole(
			new BufferedReader(new InputStreamReader(System.in)),
			System.out);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DeleteCommand deleteCommand(Lazy<DeleteCommandExecutor> deleteCommandExecutor) {
		return new DeleteCommand(deleteCommandExecutor);
	}

	@Bean
	public DeleteCommandExecutor deleteCommandExecutor(
			Supplier<Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService) {
		return new DeleteCommandExecutor(s3SettingsProvider, albumService);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DownloadCommand downloadCommand(Lazy<DownloadCommandExecutor> downloadCommandExecutor) {
		return new DownloadCommand(downloadCommandExecutor);
	}

	@Bean
	public DownloadCommandExecutor downloadCommandExecutor(
			Supplier<Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService) {
		return new DownloadCommandExecutor(s3SettingsProvider, albumService);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public InitCommand initCommand(Lazy<InitCommandExecutor> initCommandExecutor) {
		return new InitCommand(initCommandExecutor);
	}

	@Bean
	public InitCommandExecutor initCommandExecutor(
			Console console,
			ApplicationSettingsRepository applicationSettingsRepository,
			S3ClientFactory s3ClientFactory) {
		return new InitCommandExecutor(console, applicationSettingsRepository, s3ClientFactory);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ListCommand listCommand(Lazy<ListCommandExecutor> listCommandExecutor) {
		return new ListCommand(listCommandExecutor);
	}

	@Bean
	public ListCommandExecutor listCommandExecutor(
			Supplier<Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService,
			Console console) {
		return new ListCommandExecutor(s3SettingsProvider, albumService, console);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MkSiteCommand mkSiteCommand(Lazy<MkSiteCommandExecutor> mkSiteCommandExecutor) {
		return new MkSiteCommand(mkSiteCommandExecutor);
	}

	@Bean
	public MkSiteCommandExecutor mkSiteCommandExecutor(
			Supplier<Result<S3Settings, String>> s3SettingsProvider,
			SiteService siteService,
			Console console) {
		return new MkSiteCommandExecutor(s3SettingsProvider, siteService, console);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public UploadCommand uploadCommand(Lazy<UploadCommandExecutor> uploadCommandExecutor) {
		return new UploadCommand(uploadCommandExecutor);
	}

	@Bean
	public UploadCommandExecutor uploadCommandExecutor(
			Supplier<Result<S3Settings, String>> s3SettingsProvider,
			AlbumService albumService) {
		return new UploadCommandExecutor(s3SettingsProvider, albumService);
	}
}
