package cloudphoto.cli.commands.mksite;

import cloudphoto.cli.*;
import cloudphoto.common.*;
import picocli.CommandLine.*;

@Command(name = "mksite")
public class MkSiteCommand extends BaseCliCommand<MkSiteCommandExecutor> {
	public MkSiteCommand(Lazy<MkSiteCommandExecutor> commandExecutor) {
		super(commandExecutor);
	}
}
