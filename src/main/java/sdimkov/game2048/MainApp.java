package sdimkov.game2048;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainApp extends Application {

	private static final Logger log = LoggerFactory.getLogger(MainApp.class);

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	public void start(Stage stage) throws IOException {
		log.info("Starting JavaFX application");

		String fxmlFile = "/fxml/game.fxml";
		log.debug("Loading FXML for main view from: {}", fxmlFile);
		FXMLLoader loader = new FXMLLoader();
		Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

		log.debug("Showing JFX scene");
		final Scene scene = new Scene(rootNode);
		scene.getStylesheets().add("/styles/game.css");
		stage.setTitle("JavaFX 2048");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

		final GameController controller = loader.getController();
		scene.setOnKeyReleased(new EventHandler<javafx.scene.input.KeyEvent>() {
			@Override
			public void handle(javafx.scene.input.KeyEvent event) {
				try {
					controller.move(Direction.fromKeyEvent(event));
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
					log.debug("User pressed invalid key: {}", event.getCode());
				}
			}
		});
	}
}
