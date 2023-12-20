package cloudphoto.filesystem;

import cloudphoto.common.valueerrorresult.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DefaultFileRepository implements FileRepository {
	private static final OpenOption[] openForWriteOptions = {
		StandardOpenOption.CREATE_NEW,
		StandardOpenOption.WRITE
	};
	private static final OpenOption[] openForOverwriteOptions = {
		StandardOpenOption.CREATE,
		StandardOpenOption.WRITE
	};

	@Override
	public Result<List<String>, String> getFileNames(String directoryPathString, String[] regexes) {
		var directoryPath = Path.of(directoryPathString);
		var regexFilter = createRegexFilter(directoryPath.getFileSystem(), regexes);

		var fileNames = new ArrayList<String>();
		try (var fileNamesStream = Files.newDirectoryStream(directoryPath, regexFilter)) {
			for (var fileFullPath : fileNamesStream)
				fileNames.add(fileFullPath.getFileName().toString());
		} catch (NoSuchFileException e) {
			return Result.fail("Directory \"" + directoryPathString + "\" is not found");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return Result.success(fileNames);
	}

	@Override
	public Result<byte[], String> readFile(String directoryPath, String fileName) {
		try {
			return Result.success(Files.readAllBytes(Path.of(directoryPath, fileName)));
		} catch (IOException e) {
			return Result.fail(e.getMessage());
		}
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> writeFile(
			String directoryPath,
			String fileName,
			byte[] fileContent,
			boolean allowOverwrite) {
		try {
			Files.write(
				Path.of(directoryPath, fileName),
				fileContent,
				allowOverwrite ? openForOverwriteOptions : openForWriteOptions);
			return cloudphoto.common.errorresult.Result.success();
		} catch (IOException e) {
			return cloudphoto.common.errorresult.Result.fail(e.getMessage());
		}
	}

	private static DirectoryStream.Filter<Path> createRegexFilter(
			FileSystem fileSystem,
			String[] regexes) {
		var regexMatchers = Arrays.stream(regexes)
			.map(regex -> fileSystem.getPathMatcher("regex:" + regex))
			.toArray(PathMatcher[]::new);

		return path -> {
			for (var regexMatcher : regexMatchers)
				if (regexMatcher.matches(path))
					return true;

			return false;
		};
	}
}
