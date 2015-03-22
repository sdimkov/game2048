package sdimkov.game2048;


import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GameController {

	private static final Logger log = LoggerFactory.getLogger(GameController.class);

	private Tile[][] tiles = new Tile[4][4];
	private @FXML GridPane grid;
	private BlockingQueue<Direction> moves = new ArrayBlockingQueue<Direction>(25);

	public void initialize() {
		newGame();

		// Execute user moves stored in the queue
		Task executeMoves = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				while (true) {
					final Direction move = moves.take();
					final BlockingQueue<Boolean> moveResult =
							new ArrayBlockingQueue<Boolean>(1);

					// Make the move in the FX UI thread
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							moveResult.add(makeMove(move));
						}
					});

					// Wait animations to finish if actual move was performed
					if (moveResult.take()) Thread.sleep(150);
				}
			}
		};
		Thread thread = new Thread(executeMoves);
		thread.setDaemon(true);
		thread.start();
	}

	public synchronized void newGame() {
		// Delete all tiles from previous games
		for (int x=0; x<4; x++)
			for (int y=0; y<4; y++)
				if(tiles[x][y] != null) {
					grid.getChildren().remove(tiles[x][y]);
					tiles[x][y] = null;
				}

		// Add two starting tiles
		addNewTile();
		addNewTile();
	}

	public synchronized void move(Direction direction) {
		// Add new move to the queue
		try {
			moves.add(direction);
		} catch (IllegalStateException e) {
			log.error("Moves queue capacity limit reached!", e);
		}
	}

	/* Translate coordinates based on direction. The algorithm of makeMove() is written for
		direction=LEFT. Rotate coordinates for all other directions in order to apply
		the algorithm without modifications or extra if-s. */
	private Position translate(Direction direction, int x, int y) {
		switch (direction) {
			case RIGHT: return new Position(3-x, y);
			case DOWN:  return new Position(y, 3-x);
			case UP:    return new Position(3-y, x);
			default:    return new Position(x,y);
		}
	}

	private boolean makeMove(Direction direction) {
		boolean moveMade = false;
		for (int row = 0; row < 4; row++) {
			int target=0, column=1;
			while (column < 4) {
				if (target == column) {
					column++;
					if (column == 4) break;
				}
				
				Position from = translate(direction, column, row);
				Position to   = translate(direction, target, row);

				if (tiles[from.x][from.y] == null) {
					column++;
					continue;
				}
				if (tiles[to.x][to.y] == null) {
					tiles[from.x][from.y].moveTo(grid.lookup("#" + to.x + to.y), null);
					tiles[to.x][to.y] = tiles[from.x][from.y];
					tiles[from.x][from.y] = null;
					column++;
					moveMade = true;
				}
				else {
					if (tiles[to.x][to.y].getValue() == tiles[from.x][from.y].getValue()) {
						final Tile targetTile = tiles[to.x][to.y];
						final Tile movingTile = tiles[from.x][from.y];
						movingTile.moveTo(targetTile, new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								grid.getChildren().remove(movingTile);
								targetTile.promote();
							}
						});
						tiles[from.x][from.y] = null;
						column++;
						moveMade = true;
					}
					target++;
				}
			}
		}
		if (moveMade) addNewTile();
		return moveMade;
	}

	private List<Position> getUnoccupiedTiles() {
		List<Position> unoccupiedTiles = new ArrayList<Position>();
		for (int x=0; x<4; x++)
			for (int y=0; y<4; y++)
				if(tiles[x][y] == null) unoccupiedTiles.add(new Position(x,y));
		if (unoccupiedTiles.size() == 0) throw new GameLostException();
		return unoccupiedTiles;
	}

	private void addNewTile() {
		// Choose a random free spot on the game grid
		List<Position> unoccupiedTiles = getUnoccupiedTiles();
		int index = (int) (Math.random()*unoccupiedTiles.size());

		// Choose randomly between 2 and 4 tile
		Position pos = unoccupiedTiles.get(index);
		tiles[pos.x][pos.y] = ((int) (Math.random()*2) == 0) ? new Tile(2) : new Tile(4);

		// Place the new tile
		grid.add(tiles[pos.x][pos.y], pos.x, pos.y);
		tiles[pos.x][pos.y].appear();
	}
}

class Position {
	Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	int x, y;
}
