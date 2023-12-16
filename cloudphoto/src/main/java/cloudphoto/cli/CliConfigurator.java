package cloudphoto.cli;

import cloudphoto.cli.commands.init.*;
import cloudphoto.common.Lazy;
import cloudphoto.configuration.settings.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.*;

import java.io.*;

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
	public InitCommand initCommand(Lazy<InitCommandExecutor> initCommandExecutor) {
		return new InitCommand(initCommandExecutor);
	}

	@Bean
	public InitCommandExecutor initCommandExecutor(
			Console console,
			ApplicationSettingsRepository applicationSettingsRepository) {
		return new InitCommandExecutor(console, applicationSettingsRepository);
	}
}
