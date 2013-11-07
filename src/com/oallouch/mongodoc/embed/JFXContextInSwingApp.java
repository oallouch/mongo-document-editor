package com.oallouch.mongodoc.embed;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javax.swing.JComponent;

public class JFXContextInSwingApp extends JComponent {
	public static JFXContextInSwingApp instance;
	
	private JFXPanel jfxPanel;
	
	public JFXContextInSwingApp() {
		instance = this;
		setPreferredSize(new Dimension(1, 1));
		setLayout(new BorderLayout());
		jfxPanel = new JFXPanel();
		add(jfxPanel);
		Platform.runLater(() -> {
			jfxPanel.setScene(new Scene(new BorderPane()));
		});
	}
	
	public static Window getJavaFXWindow() {
		return instance.jfxPanel.getScene().getWindow();
	}
}
