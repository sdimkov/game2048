package sdimkov.game2048;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;



public class Tile extends StackPane {

	public static double tileSize = 105;

	private int value;
	private Rectangle rectangle;
	private Text text;

	public Tile(int value) {
		this.value = value;
		rectangle = new Rectangle(tileSize, tileSize);
		text = new Text(String.valueOf(value));
		init();
		this.getChildren().addAll(rectangle, text);
	}

	private void init() {
		rectangle.getStyleClass().setAll("tile", "tile-" + value);
		text.getStyleClass().setAll("tile-text", "text-" + value);
		text.setText(String.valueOf(value));
	}

	public int getValue() {
		return value;
	}

	public void appear() {
		ScaleTransition st = new ScaleTransition(Duration.millis(200), this);
		st.setFromX(0.2);
		st.setFromY(0.2);
		st.setToX(1);
		st.setToY(1);
		st.play();
	}

	public void promote() {
		value *= 2;
		init();
		ScaleTransition st = new ScaleTransition(Duration.millis(140), this);
		st.setFromX(1.3);
		st.setFromY(1.3);
		st.setToX(1);
		st.setToY(1);
		st.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if (value == 2048) throw new GameWonException();
			}
		});
		st.play();
	}

	public void moveTo(Node tile, EventHandler<ActionEvent> onFinished) {
		TranslateTransition tt = new TranslateTransition(Duration.millis(120), this);
		tt.setToX(tile.getLayoutX() + tile.getTranslateX() - this.getLayoutX());
		tt.setToY(tile.getLayoutY() + tile.getTranslateY() - this.getLayoutY());
		tt.setOnFinished(onFinished);
		tt.play();
	}
}
