package cloudphoto.cli;

import cloudphoto.common.errorresult.*;

import java.util.concurrent.*;

public interface CliCommand extends Callable<Result<String>> {
}
