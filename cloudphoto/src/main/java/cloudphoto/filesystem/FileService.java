package cloudphoto.filesystem;

import cloudphoto.common.valueerrorresult.*;

import java.util.*;

public class FileService {
	private static final String[] photoPathRegexes = { "(?i).*\\.jpe?g" };

	private final FileRepository fileRepository;

	public FileService(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}

	public Result<List<String>, String> getPhotoFileNames(String directoryPath) {
		return fileRepository.getFileNames(directoryPath, photoPathRegexes);
	}

	public Result<byte[], String> loadPhoto(String directoryPath, String photoFileName) {
		return fileRepository.readFile(directoryPath, photoFileName);
	}

	public cloudphoto.common.errorresult.Result<String> savePhoto(
			String directoryPath,
			String photoFileName,
			byte[] photoContent) {
		return fileRepository.writeFile(directoryPath, photoFileName, photoContent, true);
	}
}
