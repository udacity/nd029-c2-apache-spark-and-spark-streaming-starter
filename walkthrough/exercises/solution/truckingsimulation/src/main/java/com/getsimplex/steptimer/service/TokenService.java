package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.LoginToken;
import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.JedisData;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.getsimplex.steptimer.utils.JedisData.deleteFromRedis;

/**
 * Created by sean on 6/13/2017.
 */
public class TokenService {

    public static Optional<LoginToken> lookupToken(String userToken)throws Exception {
        ArrayList<LoginToken> allTokens = JedisData.getEntityList(LoginToken.class);
        Predicate<LoginToken> tokenPredicate = token -> token.getUuid().equals(userToken);
       // Predicate<LoginToken> activePredicate = token -> token.getExpires() && token.getExpiration().after(new Date());
        Optional<LoginToken> tokenOptional = allTokens.stream().filter(tokenPredicate).findFirst();

        return tokenOptional;
    }

    public static Optional<LoginToken> renewToken(String userToken)throws Exception {
        Optional<LoginToken> expiredTokenOptional = lookupToken(userToken);
        if (expiredTokenOptional.isPresent() && expiredTokenOptional.get().getExpires() && expiredTokenOptional.get().getExpiration().before(new Date())){
            LoginToken expiredToken = expiredTokenOptional.get();
            JedisData.deleteFromRedis(expiredToken);//should delete as long as object was not modified since reading from redis
            expiredToken.setExpiration(new Date(System.currentTimeMillis()+Long.valueOf(10*60*1000)));
            JedisData.loadToJedis(expiredToken,LoginToken.class);
        }
        return expiredTokenOptional;
    }

    public static Optional<User> getUserFromToken(String userToken) throws Exception{
        Optional<User> foundUser = Optional.empty();

        Optional<LoginToken> tokenOptional = lookupToken(userToken);
        if (tokenOptional.isPresent()){
            LoginToken loginToken = tokenOptional.get();
            foundUser = Optional.of(UserService.getUser(loginToken.getUser()));
        }
        return foundUser;
    }

    public static String createUserToken(String userName)throws Exception{
        ArrayList<User> allUsers = JedisData.getEntityList(User.class);
        Predicate<User> userPredicate = user -> user.getUserName().equals(userName);
        Predicate<User> personalTypePredicate = personal -> personal.getAccountType().equals("personal");
//        Predicate<User> businessTypePredicate = business -> business.getAccountType().equals("Business");

        Optional<User> personalOptional = allUsers.stream().filter(userPredicate).filter(personalTypePredicate).findFirst();
//        Optional<User> businessOptional = allUsers.stream().filter(userPredicate).filter(businessTypePredicate).findFirst();

        String tokenString = UUID.randomUUID().toString();
        Long currentTimeMillis = System.currentTimeMillis();
        LoginToken token = new LoginToken();
        token.setExpires(true);
        token.setUuid(tokenString);
        token.setUser(userName);

        if (personalOptional.isPresent()) {
            Long expiration = currentTimeMillis + 10 * 60 * 1000;  // expires after 10 minutes
            Date expirationDate = new Date(expiration);
            token.setExpiration(expirationDate);

        }else{
            Long expiration = currentTimeMillis + 60 * 60 * 1000;  // expires after 1 hours (business account)
            Date expirationDate = new Date(expiration);
            token.setExpiration(expirationDate);
        }
        JedisData.loadToJedis(token, LoginToken.class);
        return tokenString;

    }

    public static Boolean validateToken(String tokenString) throws Exception{
        ArrayList<LoginToken> allTokens = JedisData.getEntityList(LoginToken.class);
        Predicate<LoginToken> tokenPredicate = token -> token.getUuid().equals(tokenString);
        Optional<LoginToken> matchingToken=allTokens.stream().filter(tokenPredicate).findFirst();

        Boolean expired = false;
        if (matchingToken.isPresent() && matchingToken.get().getExpires() && matchingToken.get().getExpiration().before(new Date())){
            expired = true;
        }

        return expired;
    }
}
