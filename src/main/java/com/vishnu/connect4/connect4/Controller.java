package com.vishnu.connect4.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLEDIAMETER = 80;

	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#3CAA88";

	private static String PLAYER_ONE = "Player One";
	private static String PLAYER_TWO = "Player Two";

	private boolean isPlayerOneTurn = true;

	private final Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Label playerNameLabel;
	@FXML
	public TextField player1Name;
	@FXML
	public TextField player2Name;
	@FXML
	public Button setNamesBtn;

	private boolean isAllowedToInsert =  true;


	public void createPlayground() {

		Shape rectangleWithHoles = createGameStructureGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();

		for (Rectangle rectangle :rectangleList) {
			rootGridPane.add(rectangle,0,1);
		}

		setNamesBtn.setOnAction(actionEvent -> {
			PLAYER_ONE = player1Name.getText();
			PLAYER_TWO =  player2Name.getText();
		});
	}

	private Shape createGameStructureGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLEDIAMETER, (ROWS + 1) * CIRCLEDIAMETER);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLEDIAMETER / 2);
				circle.setCenterX(CIRCLEDIAMETER / 2);
				circle.setCenterY(CIRCLEDIAMETER / 2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (CIRCLEDIAMETER + 5) + CIRCLEDIAMETER / 4);
				circle.setTranslateY(row * (CIRCLEDIAMETER + 5) + CIRCLEDIAMETER / 4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns(){

		List<Rectangle>rectangleList = new ArrayList<>();

		for (int col = 0; col < COLUMNS; col++) {
			Rectangle rectangle = new Rectangle(CIRCLEDIAMETER,(ROWS + 1) * CIRCLEDIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLEDIAMETER + 5) + CIRCLEDIAMETER / 4);
			rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

			final int column =  col;
			rectangle.setOnMouseClicked(mouseEvent -> {
				if (isAllowedToInsert) {
					isAllowedToInsert =  false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc,int column) {

		int row = ROWS - 1;
		while (row >= 0){
			if (getDiscIfPresent(row,column)==null)
				break;

			row--;
		}

		if (row<0)
			return;

		insertedDiscsArray[row][column]=disc;
		insertedDiscPane.getChildren().add(disc);

		int currentRow = row;
		disc.setTranslateX(column * (CIRCLEDIAMETER + 5) + CIRCLEDIAMETER / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row * (CIRCLEDIAMETER + 5) + CIRCLEDIAMETER / 4);

		translateTransition.setOnFinished(actionEvent -> {

			isAllowedToInsert = true;

			if(gameEnded(currentRow,column)){
				gameOver();
				return;
			}

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE:PLAYER_TWO);
		});

		translateTransition.play();

	}

	private void gameOver() {
		String winner =  isPlayerOneTurn? PLAYER_ONE:PLAYER_TWO;
		System.out.println("Winner is:"+winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect 4");
		alert.setHeaderText("Thee Winner is "+winner);
		alert.setContentText("Want to Play again?");

		ButtonType yesBtn  = new ButtonType("Yes");
		ButtonType noBtn =  new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(() -> {

			Optional<ButtonType>btnClicked = alert.showAndWait();
			if (btnClicked.isPresent() && btnClicked.get()==yesBtn){
				resetGame();
			}else {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {

		insertedDiscPane.getChildren().clear();
		for (int row = 0; row < insertedDiscsArray.length; row++) {
			int col;
			for (col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col]=null;
			}
		}
		isPlayerOneTurn = true;
		playerNameLabel.setText(PLAYER_ONE);
		createPlayground();
	}

	private boolean gameEnded(int row, int column) {

		//vertical combinations
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3).mapToObj(r -> new Point2D(r,column))
				.collect(Collectors.toList());
        //horizontal combinations
		List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3,column+3).mapToObj(c -> new Point2D(row,c))
				.collect(Collectors.toList());
		//Diagnol combinations
		Point2D startPoint1 = new Point2D(row - 3,column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint1.add(i,-i))
				.collect(Collectors.toList());
		//Diagnol combinations
		Point2D startPoint2 = new Point2D(row - 3,column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint2.add(i,i))
				.collect(Collectors.toList());

		boolean isEnded;
		isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;

		for (Point2D point:points) {

			int rowIndexForArray = (int) point.getX();
			int colIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray,colIndexForArray);
			if(disc != null && disc.isPlayerOneMove==isPlayerOneTurn){
				chain++;
				if (chain==4){
					return true;
			}
			}else {
				chain = 0;
			}
		}
		return false;
	}

	private Disc getDiscIfPresent(int row,int column){

		if (row>= ROWS || row<0 || column>= COLUMNS || column<0)
			return null;

		return insertedDiscsArray[row][column];

	}

	private static class Disc extends Circle{

		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove=isPlayerOneMove;
			setRadius(CIRCLEDIAMETER/2);
			setCenterX(CIRCLEDIAMETER/2);
			setCenterY(CIRCLEDIAMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1):Color.valueOf(discColor2));
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}