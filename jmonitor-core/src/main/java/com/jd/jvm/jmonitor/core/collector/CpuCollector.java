package com.jd.jvm.jmonitor.core.collector;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("cpu")
public class CpuCollector {
	private RuntimeMXBean rtBean = ManagementFactory.getRuntimeMXBean();
	
	private OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

	@Http("/cpuCollect")
	public void getCpuCollect(final HttpServletResponse resp) throws IOException {
		Map<String, Object> tmpMap = getCpuCollect();
		resp.getWriter().append(JsonUtils.objectToJson(tmpMap));
	}
	
	public Map<String, Object> getCpuCollect() {
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		
		if(osBean instanceof com.sun.management.OperatingSystemMXBean) {
			com.sun.management.OperatingSystemMXBean osBean1 = (com.sun.management.OperatingSystemMXBean)osBean;
			tmpMap.put("processCpuTime", osBean1.getProcessCpuTime());
			tmpMap.put("cpuTime",rtBean.getUptime());
			tmpMap.put("availableProcessorCpuTime", osBean1.getAvailableProcessors());
			//resultMap.put("success", true);
			//resultMap.put("data", tmpMap);
		} else {
			//resultMap.put("success", false);
			//resultMap.put("data", "Failed to get cpuTime.");
		}
		
		return tmpMap;
	}
	
}
