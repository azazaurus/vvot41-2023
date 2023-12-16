package cloudphoto.cli.commands.init;

import cloudphoto.cli.*;
import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;

public class InitCommandExecutor implements CliCommandExecutor<InitCommand> {
	private static final String accessKeyIdPrompt = "Enter access key ID: ";
	private static final String secretAccessKeyPrompt = "Enter secret access key: ";
	private static final String bucketNamePrompt = "Enter bucket name: ";

	private final Console console;
	private final ApplicationSettingsRepository applicationSettingsRepository;

	public InitCommandExecutor(Console console, ApplicationSettingsRepository applicationSettingsRepository) {
		this.console = console;
		this.applicationSettingsRepository = applicationSettingsRepository;
	}

	@Override
	public Result<String> execute(InitCommand initCommand) {
		var s3Settings = promptS3Settings();
		var settingsWriteResult = applicationSettingsRepository.writeS3Settings(s3Settings);
		if (settingsWriteResult.isFailure())
			return settingsWriteResult;

		return Result.success();
	}

	private S3Settings promptS3Settings() {
		var s3Settings = S3Settings.createDefault();
		s3Settings.accessKeyId = console.prompt(accessKeyIdPrompt);
		s3Settings.secretAccessKey = console.promptPassword(secretAccessKeyPrompt);
		s3Settings.bucketName = console.prompt(bucketNamePrompt);
		return s3Settings;
	}
}
