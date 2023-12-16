package cloudphoto.cli;

public interface Console {
	String prompt(String prompt);

	String promptPassword(String prompt);
}
