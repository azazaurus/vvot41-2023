package cloudphoto.cli.commands.list;

import cloudphoto.cli.*;
import cloudphoto.common.*;
import picocli.CommandLine.*;

@Command(name = "list")
public class ListCommand extends BaseCliCommand<ListCommandExecutor> {
	@Option(names = "--album", description = "album name")
	public String albumName;

	public ListCommand(Lazy<ListCommandExecutor> commandExecutor) {
		super(commandExecutor);
	}
}
