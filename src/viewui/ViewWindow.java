package viewui;

import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/*
 * Create a new window and show the current state and
 * some stats 
 */
// TODO all stuff
public class ViewWindow extends Application {
	
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static ViewWindow viewWindow;
	
	public ViewWindow() {
		setViewWindow(this);
	}
	
	// --- Get up class ---
	
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

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println(" >>> start methode UI");
		
		BorderPane pane = new BorderPane();
		Scene scene = new Scene(pane, 512, 512);
		primaryStage.setScene(scene);
		
		Label label = new Label("Hello World");
		pane.setCenter(label);
		
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	// --- Get up class END---
}
