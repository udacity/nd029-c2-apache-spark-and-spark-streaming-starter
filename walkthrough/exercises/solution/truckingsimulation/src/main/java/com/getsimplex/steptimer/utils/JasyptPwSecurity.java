package com.getsimplex.steptimer.utils;

import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * Created by Administrator on 3/30/2017.
 */
public class JasyptPwSecurity {

    public static StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();


    public static String encrypt(String userPassword) {
        String encryptedPassword = passwordEncryptor.encryptPassword(userPassword);

        return encryptedPassword;

    }

    public static Boolean checkPw(String inputPassword, String encryptedPassword) {

        if(passwordEncryptor.checkPassword(inputPassword,encryptedPassword)){
        // correct!
        return true;
    } else{
        // bad login!
        return false;
    }
}

}
