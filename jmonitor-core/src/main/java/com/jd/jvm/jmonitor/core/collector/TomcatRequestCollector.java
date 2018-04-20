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

import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.model.TomcatRequestModel;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("request")
public class TomcatRequestCollector {
	private MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	
	@Http("/requestInfo")
	public void getRequestInfo(final HttpServletResponse resp) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException, IOException {
		List<TomcatRequestModel> modelList = getRequestInfo();
		
		resp.getWriter().append(JsonUtils.objectToJson(modelList));
	}
	
	public List<TomcatRequestModel> getRequestInfo() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException {
		Set<ObjectName> list = server.queryNames(null, null);
		List<TomcatRequestModel> modelList = new ArrayList<TomcatRequestModel>();
		for(ObjectName on : list) {
			if("GlobalRequestProcessor".equals(on.getKeyProperty("type"))) {
				
				TomcatRequestModel model = new TomcatRequestModel();
				model.setName(on.getKeyProperty("name"));
				model.setBytesSent(Long.valueOf(String.valueOf(server.getAttribute(on, "bytesSent"))));
				model.setBytesReceived(Long.valueOf(String.valueOf(server.getAttribute(on, "bytesReceived"))));
				model.setErrorCount(Long.valueOf(String.valueOf(server.getAttribute(on, "errorCount"))));
				model.setMaxTime(Long.valueOf(String.valueOf(server.getAttribute(on, "maxTime"))));
				model.setProcessingTime(Long.valueOf(String.valueOf(server.getAttribute(on, "processingTime"))));
				model.setRequestCount(Long.valueOf(String.valueOf(server.getAttribute(on, "requestCount"))));
				
				modelList.add(model);
			}
		}
		
		return modelList;
	}
}
