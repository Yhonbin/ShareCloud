package com.firefly.sharemount;

import com.firefly.sharemount.utils.JwtUtil;

import java.util.Map;

public class Main {


    public static void main(String[] args) {
       Map<String,Object> j = JwtUtil.parseToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsidXNlcklkIjoiNCJ9LCJleHAiOjE3MjUyOTM0Nzl9.YrsIAwPF8YCOBbad8V1-zPf5baE28VwvdZCgrfaK69Q");
       System.out.println(j.get("userId"));
    }
}
