package com.firefly.sharemount.utils;

import java.util.Deque;
import java.util.LinkedList;

public class PathUtil {
    public static Deque<String> pathToQueue(String path) {
        LinkedList<String> ret = new LinkedList<>();
        String[] pathArray = path.split("/");
        for (String p : pathArray) {
            if (p.isEmpty() || p.equals(".")) continue;
            boolean allDotFlag = false;
            for (int i = 0; !allDotFlag && i < p.length(); i++) {
                allDotFlag = p.charAt(i) != '.';
            }
            if (!allDotFlag) {
                if (!ret.isEmpty()) ret.pollLast();
            } else ret.offerLast(p);
        }
        return ret;
    }
}
