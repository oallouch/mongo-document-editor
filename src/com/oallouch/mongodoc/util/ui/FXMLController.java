package com.oallouch.mongodoc.util.ui;

import javafx.fxml.FXML;

/**
 * @author Romain
 * 
 * A simple controller interface who force the dev to
 * implements initialize() wich is caled by fxml at loading
 * time
 */
public interface FXMLController {
     @FXML
     abstract void initialize();
}
