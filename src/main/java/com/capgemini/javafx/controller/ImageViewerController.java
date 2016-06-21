package com.capgemini.javafx.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageViewerController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	ImageView image;
	
	@FXML
	Button previous;
	
	@FXML
	Button next;
	
	@FXML
	Button openButton;
	
	private Desktop desktop = Desktop.getDesktop();
	
	public ImageViewerController() {
		
	}
	
	@FXML
	public void initialize() {
		image.setImage(new Image("/images/Desert.jpg"));
	}
	
	@FXML
	private void openButtonAction(ActionEvent event) {
		final Stage primaryStage = null;
     
               FileChooser fileChooser = new FileChooser();
               FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Images", "*.*");
               fileChooser.getExtensionFilters().addAll(
            		    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            		    new FileChooser.ExtensionFilter("GIF", "*.gif"),
            		    new FileChooser.ExtensionFilter("BMP", "*.bmp"),
            		    new FileChooser.ExtensionFilter("PNG", "*.png"));
               File file = fileChooser.showOpenDialog(primaryStage);
               File currentPath = new File(file.getParent());
               String currentFolder= "/" + currentPath.getName().toString();
               setImage(currentFolder);
       };
	
      private void setImage(String filePath) {
    	  image.setImage(new Image(filePath));
      }
}
