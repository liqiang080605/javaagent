package com.jd.jvm.jmonitor.core.collector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.servlet.http.HttpServletResponse;

import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("tomcatInfo")
public class TomcatAllCollector {
	
	private static JvmAllCollector jvmAllCollector = new JvmAllCollector();
	private static TomcatRequestCollector tomcatRequestCollector = new TomcatRequestCollector();
	private static TomcatThreadCollector tomcatThreadCollector = new TomcatThreadCollector();
	
	@Http("/allInfo")
	public void getAllInfo(final HttpServletResponse resp) throws AttributeNotFoundException, InstanceNotFoundException, IntrospectionException, MBeanException, ReflectionException, IOException {
		
		Map<String, Object> resultMap = getAllInfo();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("success", true);
		dataMap.put("data", resultMap);
		resp.getWriter().append(JsonUtils.objectToJson(dataMap));
	}
	
	public Map<String, Object> getAllInfo() throws AttributeNotFoundException, InstanceNotFoundException, IntrospectionException, MBeanException, ReflectionException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> tomcatRequestMap = new HashMap<String, Object>();
		Map<String, Object> tomcatThreadMap = new HashMap<String, Object>();
		
		resultMap.put("jvm",jvmAllCollector.getAllInfo());
		
		tomcatRequestMap.put("requestInfo", tomcatRequestCollector.getRequestInfo());
		resultMap.put("request", tomcatRequestMap);
		
		tomcatThreadMap.put("threadInfo", tomcatThreadCollector.getThreadInfo());
		resultMap.put("thread", tomcatThreadMap);
		
		return resultMap;
	}
}
