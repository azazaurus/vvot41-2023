package cloudphoto.filesystem;

import cloudphoto.common.valueerrorresult.*;

import java.util.*;

public interface FileRepository {
	Result<List<String>, String> getFileNames(String directoryPath, String[] regexes);

	Result<byte[], String> readFile(String directoryPath, String fileName);

	cloudphoto.common.errorresult.Result<String> writeFile(
		String directoryPath,
		String fileName,
		byte[] fileContent,
		boolean allowOverwrite);
}
