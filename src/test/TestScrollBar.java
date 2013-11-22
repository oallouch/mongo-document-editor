package test;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TestScrollBar extends Application {
	@Override
	public void start(Stage primaryStage) {
		Label valueLabel = new Label();
		ScrollBar scrollBar = new ScrollBar();
		scrollBar.setOrientation(Orientation.VERTICAL);
		scrollBar.setMin(0);
		scrollBar.setMax(100);
		
		scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
			valueLabel.setText(newValue.toString());
		});
		
		Button smallerMaxButton = new Button("Max from 100 to 50");
		smallerMaxButton.setOnAction(actionEvent -> {
			scrollBar.setMax(50);
		});
		
		scrollBar.setValue(100);
		
		// uncomment to make it work
		/*scrollBar.maxProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.doubleValue() < oldValue.doubleValue() && scrollBar.getValue() > newValue.doubleValue()) {
				scrollBar.setValue(newValue.doubleValue());
			}
		});*/
		
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(valueLabel);
		borderPane.setRight(scrollBar);
		borderPane.setBottom(smallerMaxButton);
		Scene scene = new Scene(borderPane, 1000, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
