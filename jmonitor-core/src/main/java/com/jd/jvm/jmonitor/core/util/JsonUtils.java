package com.jd.jvm.jmonitor.core.util;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	@SuppressWarnings("unchecked")
	public static List<Map> jsonToList(String json){
		List<Map> result = JSON.parseArray(json, Map.class);
		return result;
	}
	
	public static String objectToJson(Object o) {
		return JSON.toJSONString(o);
	}
}
