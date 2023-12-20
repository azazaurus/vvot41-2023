package cloudphoto.logs;

public interface Log {
	void warn(String message);

	void error(String message);
}
