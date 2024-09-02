package com.firefly.sharemount.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String KEY = "firefly";
    //过期时间
    private static final Integer TIME_OUT_MINUTES = 30;

    //接收业务数据,生成token并返回
    public static String genToken(Map<String, Object> claims) {


        return JWT.create()
                .withClaim("claims",claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * TIME_OUT_MINUTES))
                .sign(Algorithm.HMAC256(KEY));

    }

    //接收token,验证token,并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }

    public static BigInteger getUserId(String token) {
        return (BigInteger) JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap()
                .get("userId");
    }
}
