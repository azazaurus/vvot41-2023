package cloudphoto.cli;

import java.io.*;

public class IoStreamConsole implements Console {
	private final BufferedReader reader;
	private final PrintStream writeStream;

	public IoStreamConsole(BufferedReader reader, PrintStream writeStream) {
		this.reader = reader;
		this.writeStream = writeStream;
	}

	@Override
	public synchronized String prompt(String prompt) {
		synchronized (writeStream) {
			writeStream.print(prompt);
			writeStream.flush();
		}

		try {
			return reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String promptPassword(String prompt) {
		return prompt(prompt);
	}
}
