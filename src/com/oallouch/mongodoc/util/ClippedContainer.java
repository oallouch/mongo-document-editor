package com.oallouch.mongodoc.util;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class ClippedContainer extends Pane {
	
	public ClippedContainer(Node content) {
		getChildren().add(content);
		
		Rectangle bottomClip = new Rectangle();
		bottomClip.setSmooth(false); // like in VirtualFlow.ClippedContainer
		bottomClip.widthProperty().bind(widthProperty());
		bottomClip.heightProperty().bind(prefHeightProperty());
		setClip(bottomClip);
	}
}
