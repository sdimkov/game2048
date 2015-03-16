package sdimkov.game2048;

import javafx.scene.input.KeyEvent;


public enum Direction {

	UP(0,-1),
	DOWN(0,1),
	LEFT(-1,0),
	RIGHT(1,0);

	public final int dx, dy;

	Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public static Direction fromKeyEvent(KeyEvent event) {
		switch (event.getCode()) {
			case UP:    return UP;
			case DOWN:  return DOWN;
			case LEFT:  return LEFT;
			case RIGHT: return RIGHT;
			default:    throw new IllegalArgumentException("Key event is not a direction. code=" + event.getCode());
		}
	}
}