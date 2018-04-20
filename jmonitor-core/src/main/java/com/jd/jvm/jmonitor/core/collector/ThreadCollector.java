package com.jd.jvm.jmonitor.core.collector;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("threads")
public class ThreadCollector {
	private ThreadMXBean tBean = ManagementFactory.getThreadMXBean();

	@Http("/threadsCount")
	public void getThreadsCount(final HttpServletResponse resp) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		
		resp.getWriter().append(JsonUtils.objectToJson(map));
	}
	
	public Map<String, Object> getThreadsCount() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("threadCount", tBean.getThreadCount());
		map.put("daemonThreadCount", tBean.getDaemonThreadCount());
		
		return map;
	}
	
}
