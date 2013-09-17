/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oallouch.mongodoc.util.ui;

import javafx.scene.control.TextArea;

/**
 *
 * @author Romain
 */
public class WindowLogger {
    private static WindowLogger instance;
    
    private TextArea logArea;
    
    private WindowLogger() {
    }
    
    public static WindowLogger getLogger() {
        if(instance == null) {
            instance = new WindowLogger();
        }
        return instance;
    }
    
    public void setLogArea(TextArea logArea) {
        this.logArea = logArea;
    }
    
    public void toLog(String text) {
        if(logArea == null) System.out.println(text);
        else logArea.appendText(text);
    }
}
