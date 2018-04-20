package com.jd.jvm.jmonitor.core.collector;

import java.io.IOException;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.jd.jvm.jmonitor.core.CoreConfigure;
import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.annotation.Http.Method;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("jvmInfo")
public class JvmAllCollector {
	private static CpuCollector cpuCollector = new CpuCollector();
	private static GcCollector gcCollector = new GcCollector();
	private static MemoryUsageCollector memoryUsageCollecotr = new MemoryUsageCollector();
	private static ThreadCollector threadCollector = new ThreadCollector();
	
	private RuntimeMXBean rtBean = ManagementFactory.getRuntimeMXBean();
	
	private CompilationMXBean cBean = ManagementFactory.getCompilationMXBean();
	
	@Http("/allInfo")
	public void getAllInfo(final HttpServletResponse resp) throws IOException {
		Map<String, Object> resultMap = getAllInfo();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("success", true);
		dataMap.put("data", resultMap);
		
		resp.getWriter().append(JsonUtils.objectToJson(dataMap));
	}
	
	public Map<String, Object> getAllInfo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> cpuMap = new HashMap<String, Object>();
		Map<String, Object> gcMap = new HashMap<String, Object>();
		Map<String, Object> threadMap = new HashMap<String, Object>();
		Map<String, Object> memoryMap = new HashMap<String, Object>();
		
		cpuMap.put("cpuInfo", cpuCollector.getCpuCollect());
		resultMap.put("cpu",cpuMap);
		
		gcMap.put("gcInfo", gcCollector.getGcCollect());
		resultMap.put("gc", gcMap);
		
		threadMap.put("threadInfo", threadCollector.getThreadsCount());
		resultMap.put("thread", threadMap);
		
		memoryMap.put("codeCache", memoryUsageCollecotr.getCodeCacheMemoryUsage());
		memoryMap.put("heap", memoryUsageCollecotr.getHeapMemoryUsage());
		memoryMap.put("nonHeap", memoryUsageCollecotr.getNonHeapMemoryUsage());
		memoryMap.put("PSEdenSpace", memoryUsageCollecotr.getPSEdenSpaceMemoryUsage());
		memoryMap.put("PSOldGen", memoryUsageCollecotr.getPSOldGenMemoryUsage());
		memoryMap.put("PSPermGen", memoryUsageCollecotr.getPSPermGenMemoryUsage());
		resultMap.put("memory", memoryMap);
		
		return resultMap;
	}
	
	@Http("/basicInfo")
	public void getBasicInfo(final HttpServletResponse resp) throws IOException {
		Map<String, Object> resultMap = getBasicInfo();
		
		resp.getWriter().append(JsonUtils.objectToJson(resultMap));
	}
	
	public Map<String, Object> getBasicInfo() throws IOException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("vmName", rtBean.getName());
		resultMap.put("jvmName", rtBean.getVmName());
		resultMap.put("vmVersion", rtBean.getVmVersion());
		resultMap.put("inputArguments", rtBean.getInputArguments());
		resultMap.put("compileName", cBean.getName());
		resultMap.put("agentName",CoreConfigure.getInstance().getAgentName());
		
		return resultMap;
	}
}
