package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateAccountFormController {
    public AnchorPane root2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblUserID;
    public Label lblNewPassword;
    public Label lblConfirmPassword;

    public void initialize(){
        lblNewPassword.setVisible(false);
        lblConfirmPassword.setVisible(false);
    }

    public void lblLoginAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {

        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) this.root2.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
        primaryStage.centerOnScreen();
    }


    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        txtUserName.setDisable(false);
        txtEmail.setDisable(false);
        txtNewPassword.setDisable(false);
        txtConfirmPassword.setDisable(false);
        btnRegister.setDisable(false);

        txtUserName.requestFocus();

        autoGenerateId();
    }

    public void autoGenerateId(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement= connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1;");
            boolean isExits = resultSet.next();

            if(isExits){
                String oldID = resultSet.getString(1);
                int id = Integer.parseInt(oldID.substring(1,oldID.length()));

                id = id+1;

                if(id<10){
                    lblUserID.setText("C00"+id);
                }else if(id<100){
                    lblUserID.setText("C0"+id);
                }else {
                    lblUserID.setText("C"+id);
                }

            }else{
                lblUserID.setText("C001");
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        boolean isEqual = newPassword.equals(confirmPassword);

        if(isEqual){
            txtNewPassword.setStyle("-fx-border-color: transparent");
            txtConfirmPassword.setStyle("-fx-border-color: transparent");
            lblNewPassword.setVisible(false);
            lblConfirmPassword.setVisible(false);

            register();
        }
        else{
            txtNewPassword.setStyle("-fx-border-color: red");
            txtConfirmPassword.setStyle("-fx-border-color: red");
            lblNewPassword.setVisible(true);
            lblConfirmPassword.setVisible(true);

            txtNewPassword.clear();
            txtConfirmPassword.clear();
        }
    }

    public void register(){

        String id = lblUserID.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirmPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,password);
            preparedStatement.setObject(4,email);

            int i = preparedStatement.executeUpdate();

            if(i!=0){
                new Alert(Alert.AlertType.CONFIRMATION,"Added Susses...").showAndWait();

                Parent parent = FXMLLoader.load(getClass().getResource("../view/LoginPageForm.fxml"));

                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root2.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();

            }else{
                new Alert(Alert.AlertType.ERROR,"Something wrong...").showAndWait();
            }


        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }


    }
}
