package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPageFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String enterUserId;
    public static String enterUseName;

    public void initialize(){

    }

    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateAccountForm.fxml"));

        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Registration Form");
        primaryStage.centerOnScreen();


    }

    public void btnLoginOnAction(ActionEvent actionEvent) {

        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where username=? and password=?;");
            preparedStatement.setObject(1,userName);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isExist = resultSet.next();

            if(isExist){

                enterUserId=resultSet.getString(1);
                enterUseName=resultSet.getString(2);

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/DashboardForm.fxml"));

                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("Todo List");
                primaryStage.centerOnScreen();
            }else {
                new Alert(Alert.AlertType.ERROR,"Invalided User name or Password").showAndWait();
                txtUserName.clear();;
                txtPassword.clear();

                txtUserName.requestFocus();
            }

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

    }
}
