package cloudphoto.cli.commands.init;

import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.s3.*;

public class InitCommandExecutor implements CliCommandExecutor<InitCommand> {
	private static final String accessKeyIdPrompt = "Enter access key ID: ";
	private static final String secretAccessKeyPrompt = "Enter secret access key: ";
	private static final String bucketNamePrompt = "Enter bucket name: ";

	private final Console console;
	private final ApplicationSettingsRepository applicationSettingsRepository;
	private final S3ClientFactory s3ClientFactory;

	public InitCommandExecutor(
			Console console,
			ApplicationSettingsRepository applicationSettingsRepository,
			S3ClientFactory s3ClientFactory) {
		this.console = console;
		this.applicationSettingsRepository = applicationSettingsRepository;
		this.s3ClientFactory = s3ClientFactory;
	}

	@Override
	public Result<String> execute(InitCommand initCommand) {
		var s3Settings = promptS3Settings();
		var settingsWriteResult = applicationSettingsRepository.writeS3Settings(s3Settings);
		if (settingsWriteResult.isFailure())
			return settingsWriteResult;

		var s3Client = s3ClientFactory.create(() -> s3Settings);
		return s3Client.createBucketIfNotExists(s3Settings.bucketName);
	}

	private S3Settings promptS3Settings() {
		var s3Settings = S3Settings.createDefault();
		s3Settings.accessKeyId = console.prompt(accessKeyIdPrompt);
		s3Settings.secretAccessKey = console.promptPassword(secretAccessKeyPrompt);
		s3Settings.bucketName = console.prompt(bucketNamePrompt);
		return s3Settings;
	}
}
