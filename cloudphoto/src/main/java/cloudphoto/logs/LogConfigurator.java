package cloudphoto.logs;

import org.springframework.context.annotation.*;

@Configuration
public class LogConfigurator {
	@Bean
	public Log log() {
		return new ErrorStreamLog(System.err);
	}
}
