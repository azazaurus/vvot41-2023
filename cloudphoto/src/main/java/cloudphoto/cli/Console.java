package cloudphoto.cli;

import java.util.*;

public interface Console {
	String prompt(String prompt);

	String promptPassword(String prompt);

	void output(String line);

	void output(Collection<String> lines);
}
