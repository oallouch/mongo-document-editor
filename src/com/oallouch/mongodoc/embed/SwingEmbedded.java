package com.oallouch.mongodoc.embed;

import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SwingEmbedded extends JFXPanel {
	/**
	 * called from the Swing Thread
	 * @param fxNodeSupplier
	 */
	public SwingEmbedded(final Supplier<? extends Node> fxNodeSupplier) {
		super();
		Platform.runLater(() -> {
			Scene scene = new Scene(new BorderPane(fxNodeSupplier.get()));
			scene.getStylesheets().add("com/oallouch/mongodoc/DocumentEditor.css");
			setScene(scene);
		});
	}
}
