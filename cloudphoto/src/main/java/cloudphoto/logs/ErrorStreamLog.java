package cloudphoto.logs;

import java.io.*;

public class ErrorStreamLog implements Log {
	private final PrintStream errorStream;

	public ErrorStreamLog(PrintStream errorStream) {
		this.errorStream = errorStream;
	}

	@Override
	public void warn(String message) {
		synchronized (errorStream) {
			errorStream.println(message);
		}
	}

	@Override
	public void error(String message) {
		synchronized (errorStream) {
			errorStream.println(message);
		}
	}
}
