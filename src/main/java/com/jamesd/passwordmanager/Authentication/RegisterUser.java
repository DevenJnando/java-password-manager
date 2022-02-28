package com.jamesd.passwordmanager.Authentication;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.Models.User;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RegisterUser extends Authenticator {

    protected static Logger logger = LoggerFactory.getLogger(RegisterUser.class);

    public RegisterUser(LoginController loginController) {
        super(loginController);
    }

    public LoginController getLoginController() {
        return this.loginController;
    }

    public User getUser() {
        return this.user;
    }

    public Boolean validateFields() {
        Boolean validated = true;
        LoginController loginController = getLoginController();
        loginController.resetErrorFields();
        List<User> usersList = checkIfUserExists(loginController.getUsernameRegisterField().getText());
        List<User> emailList = checkIfEmailIsTaken(loginController.getEmailRegisterField().getText());
        if(!emailList.isEmpty()) {
            loginController.getEmailRegisterError().setTextFill(Color.RED);
            loginController.getEmailRegisterError().setText("Email is already assigned to another user.");
            validated = false;
        }
        if(!usersList.isEmpty()) {
            loginController.getUsernameRegisterError().setTextFill(Color.RED);
            loginController.getUsernameRegisterError().setText("Username is already taken.");
            validated = false;
        }
        if(loginController.usernameRegisterIsEmpty()) {
            loginController.getUsernameRegisterError().setTextFill(Color.RED);
            loginController.getUsernameRegisterError().setText("Username is empty.");
            validated = false;
        }
        if(loginController.emailRegisterIsEmpty()) {
            loginController.getEmailRegisterError().setTextFill(Color.RED);
            loginController.getEmailRegisterError().setText("Email is empty.");
            validated = false;
        }
        if(loginController.passwordRegisterIsEmpty()) {
            loginController.getPasswordRegisterError().setTextFill(Color.RED);
            loginController.getPasswordRegisterError().setText("Password is empty.");
            validated = false;
        }
        if(!loginController.confirmPasswordRegisterMatches()) {
            loginController.getConfirmPasswordRegisterError().setTextFill(Color.RED);
            loginController.getConfirmPasswordRegisterError().setText("Passwords do not match.");
            validated = false;
        }
        return validated;
    }

    public List<User> checkIfUserExists(String username) {
        List<User> entriesByUsername = MasterSQLQueries.queryUsersByUsername(username);
        return entriesByUsername;
    }

    public List<User> checkIfEmailIsTaken(String email) {
        List<User> entriesByEmail = MasterSQLQueries.queryUsersByEmail(email);
        return entriesByEmail;
    }

}
