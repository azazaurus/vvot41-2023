package cloudphoto;

import cloudphoto.configuration.*;

public class Application {
	public Application() {
	}

	public void run(String[] args) {
	}

	public static void main(String[] args) {
		var diContainer = ApplicationConfigurator.createDiContainer();
		var application = diContainer.getBean(Application.class);
		application.run(args);
	}
}
