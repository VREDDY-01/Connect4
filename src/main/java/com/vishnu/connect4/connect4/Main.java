package com.vishnu.connect4.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();

		controller = loader.getController();
		controller.createPlayground();

		MenuBar menuBar = createmenu();
		menuBar.prefWidthProperty().bind(stage.widthProperty());

		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);
		stage.setScene(scene);
		stage.setTitle("Connect 4");
		stage.setResizable(false);
		stage.show();
	}

	private MenuBar createmenu() {
		//File Menu
		Menu fileMenu = new Menu("File");

		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(actionEvent -> controller.resetGame());

		MenuItem resetGame = new MenuItem("Reset Game");
		resetGame.setOnAction(actionEvent -> controller.resetGame());

		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

		MenuItem exitGame = new MenuItem("Exit Game");
		exitGame.setOnAction(actionEvent -> exitGame());

		fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

		//Help Menu
		Menu helpMenu = new Menu("Help");

		MenuItem aboutGame = new MenuItem("About Game");
		aboutGame.setOnAction(actionEvent -> aboutGame());

		SeparatorMenuItem separator = new SeparatorMenuItem();

		MenuItem aboutMe = new MenuItem("About Me");
		aboutMe.setOnAction(actionEvent -> aboutMe());

		helpMenu.getItems().addAll(aboutGame, aboutMe);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);


		return menuBar;
	}

	private void aboutMe() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Developer");
		alert.setHeaderText("K.VISHNU TEJA");
		alert.setResizable(true);
		alert.setContentText("Hi I am a game developer.Thank You for playing.Enjoy!!!");
		alert.show();
	}

	private void aboutGame() {
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		a.setTitle("About Game");
		a.setHeaderText("How To Play?");
		a.setContentText("Connect Four is a two-player connection game in which the players first choose " +
				"a color and then take turns dropping colored discs from the top into a seven-column, " +
				"six-row vertically suspended grid. The pieces fall straight down, occupying the next " +
				"available space within the column. The objective of the game is to be the first to form a " +
				"horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved" +
				" game. The first player can always win by playing the right moves");
		a.setResizable(true);
		a.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}