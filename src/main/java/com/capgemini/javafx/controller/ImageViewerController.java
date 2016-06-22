package com.capgemini.javafx.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ImageViewerController {

	private static final Logger LOG = Logger.getLogger(ImageViewerController.class);

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

	@FXML
	Button slideShowButton;

	@FXML
	ScrollPane imageScrollPane;

	@FXML
	ListView<String> imagesList;

	private Timer timer;

	final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

	private List<File> imageFiles = new ArrayList<File>();

	private int imageListIndex;

	private boolean isSlideShowButtonClicked = false;

	public ImageViewerController() {

	}

	@FXML
	public void initialize() {
		initialImage();

		zoomProperty.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				image.setFitWidth(zoomProperty.get() * 4);
				image.setFitHeight(zoomProperty.get() * 3);

			}
		});

		image.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
					zoomProperty.set(zoomProperty.get() * 1.2);
				} else if (event.getDeltaY() < 0) {
					zoomProperty.set(zoomProperty.get() / 1.2);
				}
			}
		});

		imageScrollPane.setContent(image);
	}

	private void initialImage() {
		image.setImage(new Image("/images/logo.jpg"));
	}

	@FXML
	private void openButtonAction(ActionEvent event) {
		imageFiles = getImageFiles();
		if (imageFiles.isEmpty()) {
			LOG.debug("No images to display");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("No images to display");
			alert.show();
			if (timer != null){
				isSlideShowButtonClicked = false;
				timer.cancel();
			}
			initialImage();
			imagesList.getItems().clear();
		}
		if (!imageFiles.isEmpty()) {
			if (timer != null){
				isSlideShowButtonClicked = false;
				timer.cancel();
			}
			imageListIndex = 0;
			showImageFiles(imageFiles);
			String absolutePath = "file:" + imageFiles.get(imageListIndex).getAbsolutePath();
			Image selectedImage = new Image(absolutePath);
			image.setImage(selectedImage);
		}
	}

	private List<File> getImageFiles() {
		List<File> imagesList = new ArrayList<File>();
		Stage stage = new Stage();
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedFolder = directoryChooser.showDialog(stage);
		if (selectedFolder == null) {
			LOG.debug("No folder selected");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("No folder selected");
			alert.show();
		}
		if (selectedFolder != null) {
			File[] files = selectedFolder.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					String file = files[i].getName();
					if (file.endsWith(".jpg") || file.endsWith(".jpeg") || file.endsWith(".gif")
							|| file.endsWith(".png") || file.endsWith(".bmp")) {
						imagesList.add(files[i]);
					}
				}
			}
		}
		return imagesList;
	}

	private void showImageFiles(List<File> files) {
		ObservableList<String> items = FXCollections.observableArrayList();
		for (File file : files) {
			items.add(file.getName());
		}

		imagesList.setItems(items);
		imagesList.setOrientation(Orientation.HORIZONTAL);
		imagesList.setCellFactory(param -> new ListCell<String>() {
			private ImageView imageView = new ImageView();

			@Override
			public void updateItem(String name, boolean empty) {
				super.updateItem(name, empty);

				for (File file : files) {
					if (file.getName().equals(name))
						imageView.setImage(new Image("file:" + file.getAbsolutePath()));
				}
				imageView.setFitWidth(70);
				imageView.setFitHeight(70);
				setGraphic(imageView);
			}
		});

	}

	@FXML
	private void nextButtonAction(ActionEvent event) {
		displayNext();
	}

	private void displayNext() {

		if (imageFiles.isEmpty()) {
			LOG.debug("No Image to display");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("No Image to display");
			alert.show();
		} else {
			String absolutePath = "";
			if (imageListIndex < imagesList.getItems().size() - 1) {
				imageListIndex = imageListIndex + 1;
				absolutePath = "file:" + imageFiles.get(imageListIndex).getAbsolutePath();
			}
			if (imageListIndex == imagesList.getItems().size() - 1) {
				imageListIndex = 0;
				absolutePath = "file:" + imageFiles.get(imageListIndex).getAbsolutePath();
			}
			Image selectedImage = new Image(absolutePath);
			image.setImage(selectedImage);
		}
	}

	@FXML
	private void previousButtonAction(ActionEvent event) {
		displayPrevious();
	}

	private void displayPrevious() {
		if (imageFiles.isEmpty()) {
			LOG.debug("No Image to display");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("No Image to display");
			alert.show();
		} else {
			String absolutePath = "";
			if (imageListIndex > 0) {
				imageListIndex = imageListIndex - 1;
				absolutePath = "file:" + imageFiles.get(imageListIndex).getAbsolutePath();
				LOG.debug(imageFiles.get(imageListIndex).getAbsolutePath());
			}
			if (imageListIndex == 0) {
				imageListIndex = imageFiles.size() - 1;
				absolutePath = "file:" + imageFiles.get(imageListIndex).getAbsolutePath();
			}
			Image selectedImage = new Image(absolutePath);
			image.setImage(selectedImage);
		}
	}

	@FXML
	private void selectImageListViewAction() {
		displaySelectedImage();
	}

	private void displaySelectedImage() {
		if (imageFiles.isEmpty()) {
			LOG.debug("No Image to display");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("No Image to display");
			alert.show();
		} else {
			String absolutePath = "";
			for (int i = 0; i < imageFiles.size(); i++) {
				if (imagesList.getSelectionModel().getSelectedItem().equals(imageFiles.get(i).getName())) {
					absolutePath = "file:" + imageFiles.get(i).getAbsolutePath();
					imageListIndex = i;
				}
			}
			Image selectedImage = new Image(absolutePath);
			image.setImage(selectedImage);
		}
	}

	@FXML
	private void slideShowButtonAction(ActionEvent event) {
		if (imagesList.getItems().isEmpty()) {
			LOG.debug("No images to display");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("No images to display");
			alert.show();
		} else {
			if (!isSlideShowButtonClicked) {
				isSlideShowButtonClicked = true;
				imagesList.getSelectionModel().clearSelection();
				slideShow();
			} else {
				isSlideShowButtonClicked = false;
				timer.cancel();
			}
		}
	}

	private void slideShow() {
		long displayImageTime = 2000;
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				displayNext();
			}
		};
		timer.schedule(task, displayImageTime, displayImageTime);

	}
}
