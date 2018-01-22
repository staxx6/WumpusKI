package de.fh.viewui;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.fh.MyWumpusAgent;
import de.fh.State;
import de.fh.Tile;
import de.fh.TileType;
import de.fh.util.Vector2;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/*
 * Create a new window and show the current state and
 * some stats 
 * 
 * TODO rename package de.fh.viewclass
 */
// TODO all stuff
public class ViewWindow extends Application implements Runnable {

	Stage stage;

	public static final CountDownLatch latch = new CountDownLatch(1);
	public static ViewWindow viewWindow;

	private static final GridPane gp = new GridPane();
	private static final BorderPane bp = new BorderPane();

	private static final int x_range = 35;
	private static final int y_range = 35;

	public ViewWindow() {
		setViewWindow(this);
	}

	// --- Get up class ---

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.stage = primaryStage;

		System.out.println(" >>> start methode UI");
		primaryStage.setTitle("Wumpus Agent View State");

		int childs = 0;
		for (int y = 0; y < y_range; y++) {
			for (int x = 0; x < x_range; x++) {
				gp.add(new Label(x + "," + y + "\n" + childs), x, y);
				childs++;
			}
		}
		gp.setGridLinesVisible(true);
		
		ScrollPane sp = new ScrollPane();
		sp.setContent(gp);

		bp.setCenter(sp);

		Scene scene = new Scene(bp, 512, 512);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public int getXRange() {
		return x_range;
	}

	public int getYRange() {
		return y_range;
	}

	public int getGridIndex(Vector2 pos) {
		int x = pos.getX();
		int y = pos.getY();
		int index = ((y * y_range) + x);
		return index;
	}

	public int getTileNorth(int index) {
		int newIndex = index - x_range;
		return newIndex;
	}

	public int getTileEast(int index) {
		int newIndex = index + 1;
		return newIndex;
	}

	public int getTileSouth(int index) {
		int newIndex = index + x_range;
		return newIndex;
	}

	public int getTileWest(int index) {
		int newIndex = index - 1;
		return newIndex;
	}

	public void update(State state) {
		updateView(state);
		stage.getScene().setRoot(bp);
	}

	public void updateView(State state) {
		for (int y = 0; y < y_range; y++) {
			for (int x = 0; x < x_range; x++) {
				int index = ((y * x_range) + x);
				Label temp = (Label) gp.getChildren().get(y * x_range + x); 	// Aktuelles Label
				Tile temp_tile = state.getView().get(y).get(x);				// Aktuelles Tile
				if (temp_tile != null) {
					temp.setGraphic(new ImageView(getImage(temp_tile)));
					if (temp_tile.isBreeze()) {
						Label north = (Label) gp.getChildren().get(getTileNorth(index));
						north.setGraphic(new ImageView(new Image("/data/images/wumpus/pit.png")));
						north.getGraphic().setOpacity(0.5);
						Label east = (Label) gp.getChildren().get(getTileEast(index));
						east.setGraphic(new ImageView(new Image("file:///Users/andre/eclipse-workspace/Pacman_Finale/data/images/wumpus/pit.png")));
						east.getGraphic().setOpacity(0.5);
						Label south = (Label) gp.getChildren().get(getTileSouth(index));
						south.setGraphic(new ImageView(new Image("file:///Users/andre/eclipse-workspace/Pacman_Finale/data/images/wumpus/pit.png")));
						south.getGraphic().setOpacity(0.5);
						Label west = (Label) gp.getChildren().get(getTileWest(index));
						west.setGraphic(new ImageView(new Image("file:///Users/andre/eclipse-workspace/Pacman_Finale/data/images/wumpus/pit.png")));
						west.getGraphic().setOpacity(0.5);
					}
				}
			}
		}
	}
	
	public Image getPossPit(Tile tile) {
		Image img = null;
		
		tile.getPossibleTypes().contains(TileType.PIT);	// If true en....
		if (tile.getTileType() == TileType.PIT) {};	//Sure
		
		
		return img;
	}

	public static Image getImage(Tile tile) {
		Image image_gold = new Image("file:///Users/andre/eclipse-workspace/Pacman_Finale/data/images/wumpus/gold.png");
		Image image_hunter = new Image("file:///Users/andre/eclipse-workspace/Pacman_Finale/data/images/wumpus/hunter.png");
		Image image_pit = new Image("file:///Users/andre/eclipse-workspace/Pacman_Finale/data/images/wumpus/pit.png");
		Image image_wall = new Image("/data/images/wumpus/wall.png");
		Image image_wumpus = new Image("file:///Users/andre/eclipse-workspace/Pacman_Finale/data/images/wumpus/wumpus.png");

		if (tile != null) {
			if (tile.getTileType() == TileType.GOLD) {
				return image_gold;
			} else if (tile.getTileType() == TileType.PIT) {
				return image_pit;
			} else if (tile.getTileType() == TileType.WALL) {
				return image_wall;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static void setViewWindow(final ViewWindow viewWindow0) {
		viewWindow = viewWindow0;
		latch.countDown();
	}

	public static ViewWindow waitForViewWindow() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return viewWindow;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void run() {
		
	}

	// --- Get up class END---
}
