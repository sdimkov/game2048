package sdimkov.game2048;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameController {

	private static final Logger log = LoggerFactory.getLogger(GameController.class);
	private Tile[][] tiles = new Tile[4][4];
	private @FXML GridPane grid;

	public void initialize() {
		newGame();
	}

	public void newGame() {
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

	public void move(Direction direction) {
		makeMove(direction);
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

	private void makeMove(Direction direction) {
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
					}
					target++;
				}
			}
		}
		addNewTile();
	}

	private List<Position> getUnoccupiedTiles() {
		List<Position> unoccupiedTiles = new ArrayList<>();
		for (int x=0; x<4; x++)
			for (int y=0; y<4; y++)
				if(tiles[x][y] == null) unoccupiedTiles.add(new Position(x,y));
		if (unoccupiedTiles.size() == 0) throw new GameLostException();
		return unoccupiedTiles;
	}

	private void addNewTile() {
		List<Position> unoccupiedTiles = getUnoccupiedTiles();
		int index = (int) (Math.random()*unoccupiedTiles.size());
		Position pos = unoccupiedTiles.get(index);
		tiles[pos.x][pos.y] = ((int) (Math.random()*2) == 0) ? new Tile(2) : new Tile(4);
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
