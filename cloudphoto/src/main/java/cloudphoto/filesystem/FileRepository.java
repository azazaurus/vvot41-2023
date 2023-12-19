package cloudphoto.filesystem;

import cloudphoto.common.valueerrorresult.*;

import java.util.*;

public interface FileRepository {
	Result<List<String>, String> getFileNames(String directoryPath, String[] regexes);

	Result<byte[], String> readFile(String directoryPath, String fileName);
}
