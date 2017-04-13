package com.pharbers.aqll.alcalc.alCommon;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qianpeng on 2017/4/13.
 */
public class alFromJson {
	public static Map<String, String> formJson(String resp) {
		Gson gson = new Gson();
		return  gson.fromJson(resp, Map.class);
	}
}
