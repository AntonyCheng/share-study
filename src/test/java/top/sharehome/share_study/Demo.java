package top.sharehome.share_study;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        HashMap<Long, String> hashMap = new HashMap<>();
        hashMap.put(1L, "1");
        hashMap.put(2L, "2");
        String jsonStr = JSONUtil.toJsonStr(hashMap);
        System.out.println(jsonStr);
    }
}
