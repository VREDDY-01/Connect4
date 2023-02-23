module com.vishnu.connect4.connect4 {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.vishnu.connect4.connect4 to javafx.fxml;
	exports com.vishnu.connect4.connect4;
}