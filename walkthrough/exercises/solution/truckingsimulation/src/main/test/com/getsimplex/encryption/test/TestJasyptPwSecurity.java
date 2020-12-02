package com.getsimplex.encryption.test;


import com.getsimplex.steptimer.utils.JasyptPwSecurity;

/**
 * Created by Administrator on 3/23/2017.
 */
public class TestJasyptPwSecurity {

    public static void main (String[] args) throws Exception{
        String inputPw = "P@ssw0rd";
        String encryptedPassword= JasyptPwSecurity.encrypt(inputPw);
        System.out.println("Encrypted :"+encryptedPassword);
        Boolean decryptedPassword = JasyptPwSecurity.checkPw(inputPw,encryptedPassword);
        System.out.println("Decrypted :"+decryptedPassword);
    }
}
