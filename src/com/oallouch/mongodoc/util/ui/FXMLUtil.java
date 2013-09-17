package com.oallouch.mongodoc.util.ui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author Romain
 */
public class FXMLUtil {
    private FXMLUtil() {
    }
    
    /*
     * It's based on the new FXMLLoader methods for javaFX 2.2
     * because the old one (using Initialize implementation may become deprecated).
     * It's a simple utility to initialize the container class as controller by loading
     * it from an fxml file wich contain the root node of the scene. (See the controllers class)
     */
    public static void loadFromFile(Parent container, String fxmlFile) {
        // FXML loading & intiializing stuff
        FXMLLoader fxmlLoader = new FXMLLoader(FXMLUtil.class.getResource(fxmlFile));
        fxmlLoader.setRoot(container);
        fxmlLoader.setController(container);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
