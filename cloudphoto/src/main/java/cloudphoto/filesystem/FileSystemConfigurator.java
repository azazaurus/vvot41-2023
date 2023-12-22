package cloudphoto.filesystem;

import org.springframework.context.annotation.*;

@Configuration
public class FileSystemConfigurator {
	@Bean
	public FileRepository fileRepository() {
		return new DefaultFileRepository();
	}

	@Bean
	public FileService fileService(FileRepository fileRepository) {
		return new FileService(fileRepository);
	}
}
