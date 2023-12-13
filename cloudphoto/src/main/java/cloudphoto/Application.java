package cloudphoto;

import cloudphoto.common.result.*;
import cloudphoto.configuration.*;

public class Application {
	private static final int errorExitCode = 1;

	public Application() {
	}

	public Result run(String[] args) {
		return Result.success;
	}

	public static void main(String[] args) {
		try {
			var diContainer = ApplicationConfigurator.createDiContainer();
			var application = diContainer.getBean(Application.class);

			var applicationRunResult = application.run(args);
			if (applicationRunResult.isFailure())
				System.exit(errorExitCode);
		}
		catch (Throwable throwable) {
			System.err.print("Unexpected error: ");
			throwable.printStackTrace(System.err);
			System.exit(errorExitCode);
		}
	}
}
