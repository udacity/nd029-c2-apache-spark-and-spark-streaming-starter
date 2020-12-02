package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.LoginToken;
import com.getsimplex.steptimer.model.Token;
import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.JedisData;
import com.google.gson.Gson;
import spark.Request;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static com.getsimplex.steptimer.utils.JedisData.deleteFromRedis;
import static com.getsimplex.steptimer.service.TokenService.createUserToken;

/**
 * Created by Administrator on 12/7/2016.
 */
public class TokenController {

    public static String createUserToken(String userName)throws Exception{

        return createUserToken(userName);

    }
}
