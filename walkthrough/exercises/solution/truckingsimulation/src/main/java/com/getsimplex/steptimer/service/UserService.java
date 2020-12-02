package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.JedisData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by sean on 6/13/2017.
 */
public class UserService {

    public static User getUser(String userName) throws Exception{
        Predicate<User> userPredicate = user -> user.getUserName().equals(userName);
        List<User> users =getUsers();
        Optional<User> userOptional = users.stream().filter(userPredicate).findFirst();
        if (!userOptional.isPresent()){
            throw new Exception ("User name not found");
        }
        User currentUser = userOptional.get();
        return currentUser;

    }

    public static List<User> getUsers() throws Exception{
        ArrayList<User> users = JedisData.getEntityList(User.class);
        return users;
    }
}
