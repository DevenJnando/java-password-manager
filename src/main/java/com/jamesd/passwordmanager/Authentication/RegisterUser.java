package com.jamesd.passwordmanager.Authentication;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.Models.Users.User;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RegisterUser extends Authenticator {

    /**
     * Class which registers a new user and stores the user in the master CosmosDB database.
     */

    protected static Logger logger = LoggerFactory.getLogger(RegisterUser.class);

    /**
     * Constructor which takes the LoginController currently being used. This will be used to retrieve user-input values.
     * @param loginController LoginController currently being used
     */
    public RegisterUser(LoginController loginController) {
        super(loginController);
    }

    /**
     * Validation method which ensures that the email, username and password are not empty, that the email and username
     * are unique, and that the "confirm password" field matches the "enter password" field.
     * @return Returns true if all fields are successfully validated, and false if there is an error anywhere
     */
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

    /**
     * Method which checks if a username already exists in the master database
     * @param username User-input username
     * @return Returns a list of all users matching this given username (Should only ever be zero or one user)
     */
    public List<User> checkIfUserExists(String username) {
        return MasterSQLQueries.queryUsersByUsername(username);
    }

    /**
     * Method which checks if an email address already exists in the master database
     * @param email User-input email address
     * @return Returns a list of all users matching this given email address (Should only ever be zero or one user)
     */
    public List<User> checkIfEmailIsTaken(String email) {
        return MasterSQLQueries.queryUsersByEmail(email);
    }

}
