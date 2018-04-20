package com.jd.jvm.jmonitor.core.collector;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.model.TomcatThreadModel;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("thread")
public class TomcatThreadCollector {
	private static final Logger logger = LoggerFactory.getLogger(TomcatThreadCollector.class);
	
	private MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	
	@Http("/threadInfo")
	public void getThreadInfo(final HttpServletResponse resp) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException, IOException {
		List<TomcatThreadModel> modelList = getThreadInfo();
		
		resp.getWriter().append(JsonUtils.objectToJson(modelList));
	}
	
	public List<TomcatThreadModel> getThreadInfo() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException {
		Set<ObjectName> list = server.queryNames(null, null);
		List<TomcatThreadModel> modelList = new ArrayList<TomcatThreadModel>();
		for(ObjectName on : list) {
			if("ThreadPool".equals(on.getKeyProperty("type"))) {
				
				// tomcat1.6 部分指标没有，用0代替
				TomcatThreadModel model = new TomcatThreadModel();
				model.setName(on.getKeyProperty("name"));
				model.setConnectionCount(getAttributeValue(on, "connectionCount"));
				model.setCurrentThreadCount(getAttributeValue(on, "currentThreadCount"));
				model.setMaxConnections(getAttributeValue(on, "maxConnections"));
				model.setMaxKeepAliveRequests(getAttributeValue(on, "maxKeepAliveRequests"));
				model.setMaxThreads(getAttributeValue(on, "maxThreads"));
				
				modelList.add(model);
			}
		}
		
		return modelList;
	}
	
	private int getAttributeValue(ObjectName on, String attribute) {
		int v = 0;
		try {
			String value = String.valueOf(server.getAttribute(on, attribute));
			v = Integer.valueOf(value);
		} catch (Exception e) {
			logger.info("Failed to get attribute value. Attribute is " + attribute);
		}
		
		return v;
	}
}
