package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import tm.TodoTM;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DashboardFormController {
    public Label lblUserId;
    public Label lblWelcomeNote;
    public Pane subRoot;
    public TextField txtTaskName;
    public AnchorPane root;
    public ListView<TodoTM> lstTodos;
    public TextField txtSelectedDescription;
    public Button btnUpdate;
    public Button btnDelete;
    public String id;
    public Label lblTextCanNotBeEmpty;

    public void initialize(){
        lblUserId.setText(LoginPageFormController.enterUserId);
        lblWelcomeNote.setText("Hi "+ LoginPageFormController.enterUseName+ " welcome to To Do list");

        subRoot.setVisible(false);
        loadList();

        txtSelectedDescription.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        lblTextCanNotBeEmpty.setVisible(false);

        lstTodos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoTM>() {
            @Override
            public void changed(ObservableValue<? extends TodoTM> observable, TodoTM oldValue, TodoTM newValue) {
                txtSelectedDescription.setDisable(false);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);

                subRoot.setVisible(false);

                txtSelectedDescription.requestFocus();
                // because od]f the update button error..............................
                if(newValue == null){
                    return;
                }
                //.............................................................
                TodoTM selectedItem = lstTodos.getSelectionModel().getSelectedItem(); // 2 lines not need use new value

                String description = selectedItem.getDescription();  //  or String description = newValue.getDescription();

                txtSelectedDescription.setText(description);

                id = selectedItem.getId();
            }
        });


    }

    public void btnAddNewTodoOnAction(ActionEvent actionEvent) {
        subRoot.setVisible(true);
        txtTaskName.requestFocus();


        txtSelectedDescription.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        txtSelectedDescription.clear();

    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out", ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) this.root.getScene().getWindow();

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {

        if(txtTaskName.getText().trim().isEmpty()){
            lblTextCanNotBeEmpty.setVisible(true);
            txtTaskName.requestFocus();
        }else {
            lblTextCanNotBeEmpty.setVisible(false);

            String newId = autoGenerateId();
            String description = txtTaskName.getText();
            String userId = lblUserId.getText();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into todos values(?,?,?)");
                preparedStatement.setObject(1,newId);
                preparedStatement.setObject(2,description);
                preparedStatement.setObject(3,userId);

                int i = preparedStatement.executeUpdate();
                System.out.println(i);

                subRoot.setVisible(false);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            loadList();
        }

    }

    public String autoGenerateId(){

        String newId = null;

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from todos order by id desc limit 1; ");
            boolean isExits = resultSet.next();

            if(isExits){
                String oldId = resultSet.getString(1);
                oldId = oldId.substring(1, oldId.length());
                int oldIdI = Integer.parseInt(oldId);
                oldIdI++;

                if(oldIdI<10){

                    newId = "T00"+oldIdI;
                }else if(oldIdI<100) {
                    newId = "T0"+oldIdI;
                }else {
                    newId = "T"+oldIdI;
                }

            }else {
                newId = "T001";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return newId ;
    }

    public void loadList(){
        ObservableList<TodoTM> todos = lstTodos.getItems();

        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todos where user_id = ?;");
            preparedStatement.setObject(1,LoginPageFormController.enterUserId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userId = resultSet.getString(3);

                TodoTM todoTM = new TodoTM(id,description,userId);
                todos.add(todoTM);
            }
            lstTodos.refresh();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedDescription.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todos set discription=? where id=?");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);
            preparedStatement.executeUpdate();
            loadList();
            txtSelectedDescription.clear();
            txtSelectedDescription.setDisable(true);
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this....?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todos where id=?");
                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();
                loadList();
                txtSelectedDescription.setDisable(true);
                txtSelectedDescription.clear();
                btnDelete.setDisable(true);
                btnUpdate.setDisable(true);
            } catch (SQLException throwables) {

            }
        }
    }
}

