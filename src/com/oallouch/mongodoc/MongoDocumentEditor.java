package com.oallouch.mongodoc;

import com.oallouch.mongodoc.tree.DocumentTree;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MongoDocumentEditor {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Mongo Document Editor");
				final JFXPanel fxPanel = new JFXPanel();
				frame.add(fxPanel);
				frame.setBounds(100, 100, 800, 600);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Group root = new Group();
						Scene scene = new Scene(root, 300, 250, Color.WHITE);
						DocumentEditor documentEditor = new DocumentEditor();
						root.getChildren().add(documentEditor);
						fxPanel.setScene(scene);
					}
				});
			}
		});
	}
}
