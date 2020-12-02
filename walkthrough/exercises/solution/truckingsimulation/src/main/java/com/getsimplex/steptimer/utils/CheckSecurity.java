package com.getsimplex.steptimer.utils;

import com.google.gson.Gson;
import com.getsimplex.steptimer.model.LoginToken;
//import scala.util.parsing.combinator.testing.Str;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

import static com.getsimplex.steptimer.service.TokenController.createUserToken;
import static com.getsimplex.steptimer.utils.JedisData.deleteFromRedis;

/**
 * Created by Administrator on 12/29/2016.
 */
public class CheckSecurity {

    private static Gson gson = GsonFactory.getGson();


    public static String securityCheck(String token) throws Exception {
        String testToken = token;

        ArrayList<LoginToken> allTokens = JedisData.getEntityList(LoginToken.class);
        Predicate<LoginToken> tokenPredicate = newToken -> newToken.getUuid().equals(testToken);
        Optional<LoginToken> tokenOptional = allTokens.stream().filter(tokenPredicate).findAny();

        if (!tokenOptional.isPresent()) {
            return "";
        }else {
            LoginToken loginToken = tokenOptional.get();

            Boolean tokenExpires = loginToken.getExpires();

            if (tokenExpires == true && loginToken.getExpiration().before(new Date())) {
                return "";
            }
        }
        LoginToken loginToken = tokenOptional.get();

        String user = loginToken.getUser();
        deleteFromRedis(loginToken);
        String anotherToken = createUserToken(user);

        return anotherToken;

    }
}

//            Boolean dateExpires = loginToken.getExpiration().before(new Date);
//            Date dateExpires = loginToken.getExpiration();

//            if(tokenOptional.isPresent()&& tokenExpires == true && loginToken.getExpiration().after(new Date())){
//                String user = loginToken.getUser();
//                deleteFromRedis(loginToken);
//                String anotherToken = createUserToken(user);
//                return anotherToken;
//            }else{
//                return "";
//            }
//        }


