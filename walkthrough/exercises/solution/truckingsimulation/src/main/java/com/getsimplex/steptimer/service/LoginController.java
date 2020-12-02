package com.getsimplex.steptimer.service;

import com.google.gson.Gson;
import com.getsimplex.steptimer.model.LoginRequest;
import com.getsimplex.steptimer.model.LoginToken;
import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.*;
import spark.Request;
import com.getsimplex.steptimer.utils.JedisClient;
import com.getsimplex.steptimer.utils.JedisData;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static com.getsimplex.steptimer.service.TokenService.*;
import static com.getsimplex.steptimer.utils.JedisData.deleteFromRedis;

/**
 * Created by mandy on 9/22/2016.
 */

public class LoginController {
    private static Gson gson = new Gson();
    private static JedisClient jedisClient = new JedisClient();

    public static String handleRequest(Request request) throws Exception{
        String loginRequestString = request.body();
        LoginRequest loginRequest = gson.fromJson(loginRequestString, LoginRequest.class);
        return tryLogin(loginRequest.getUserName(), loginRequest.getPassword());

    }

    public static String tryLogin(String userName, String password) throws Exception{
        if(isValidPassword(userName, password)){

           //create logintoken for user - save in redis
                ArrayList<LoginToken> allTokens = JedisData.getEntityList(LoginToken.class);
                Predicate<LoginToken> tokenPredicate = token -> token.getUser().equals(userName);

                String newToken = "";
                newToken = createUserToken(userName);
                return newToken;
        }else{
            throw new Exception("Invalid Login");
        }
    }



    public static Boolean isValidPassword(String unauthenticatedName, String attemptedPwValue)throws Exception {
        boolean passwordIsValid = false;
        boolean userNameIsValid=false;
        User currentUser = UserService.getUser(unauthenticatedName);

        //use if logging in with Redis
        try {
            if (unauthenticatedName != null && !unauthenticatedName.isEmpty()) {
//
                String userName = currentUser.getUserName();

                if (userName.equals(unauthenticatedName)) {
                    userNameIsValid = true;
                }
            }

            if (userNameIsValid) {
                String userName = currentUser.getUserName();
                String encryptedPassword = currentUser.getPassword();
                Boolean pwTrue = JasyptPwSecurity.checkPw(attemptedPwValue, encryptedPassword);

                if (encryptedPassword != null && !encryptedPassword.isEmpty() && pwTrue) {
                    passwordIsValid = true;
                }
            }
        } catch (Exception e){
            Logger.getLogger(LoginController.class.getName()).severe(e.getMessage());
        }


        return (passwordIsValid && userNameIsValid);
    }



}
