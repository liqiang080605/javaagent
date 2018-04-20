package com.jd.jvm.jmonitor.core.collector;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.model.MemoryUsageModel;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("memoryUsage")
public class MemoryUsageCollector {
	private MemoryMXBean mBean = ManagementFactory.getMemoryMXBean();
	
	private List<MemoryPoolMXBean> mpBeanList = ManagementFactory.getMemoryPoolMXBeans();

	@Http("/heapMemoryUsage")
	public void getHeapMemoryUsage(final HttpServletResponse resp) throws IOException {
		MemoryUsageModel model = getHeapMemoryUsage();
		
		resp.getWriter().append(JsonUtils.objectToJson(model));
	}
	
	public MemoryUsageModel getHeapMemoryUsage() {
		MemoryUsage memoryUsage = mBean.getHeapMemoryUsage();
		MemoryUsageModel model = new MemoryUsageModel();
		model.setName("heapMemoryUsage");
		UpdateModelFromMemoryUsage(model, memoryUsage);
		
		return model;
	}
	
	@Http("/nonHeapMemoryUsage")
	public void getNonHeapMemoryUsage(final HttpServletResponse resp) throws IOException {
		MemoryUsageModel model = getNonHeapMemoryUsage();

		resp.getWriter().append(JsonUtils.objectToJson(model));
	}
	
	public MemoryUsageModel getNonHeapMemoryUsage() {
		MemoryUsageModel model = new MemoryUsageModel();
		MemoryUsage memoryUsage = mBean.getNonHeapMemoryUsage();
		model.setName("nonHeapMemoryUsage");
		UpdateModelFromMemoryUsage(model, memoryUsage);

		return model;
	}
	
	@Http("/codeCacheMemoryUsage")
	public void getCodeCacheMemoryUsage(final HttpServletResponse resp) throws IOException {
		MemoryUsageModel model = getCodeCacheMemoryUsage();

		resp.getWriter().append(JsonUtils.objectToJson(model));
	}
	
	public MemoryUsageModel getCodeCacheMemoryUsage() {
		MemoryUsageModel model = new MemoryUsageModel();
		MemoryUsage memoryUsage = getMemoryPoolBeanUsage(model,"Code");
		UpdateModelFromMemoryUsage(model, memoryUsage);

		return model;
	}
	
	@Http("/PSEdenSpaceMemoryUsage")
	public void getPSEdenSpaceMemoryUsage(final HttpServletResponse resp) throws IOException {
		MemoryUsageModel model = getPSEdenSpaceMemoryUsage();
		
		resp.getWriter().append(JsonUtils.objectToJson(model));
	}
	
	public MemoryUsageModel getPSEdenSpaceMemoryUsage() {
		MemoryUsageModel model = new MemoryUsageModel();
		MemoryUsage memoryUsage = getMemoryPoolBeanUsage(model,"Eden");
		UpdateModelFromMemoryUsage(model, memoryUsage);
		
		return model;
	}
	
	@Http("/PSOldGenMemoryUsage")
	public void getPSOldGenMemoryUsage(final HttpServletResponse resp) throws IOException {
		MemoryUsageModel model = getPSOldGenMemoryUsage();
		
		resp.getWriter().append(JsonUtils.objectToJson(model));
	}
	
	public MemoryUsageModel getPSOldGenMemoryUsage() {
		MemoryUsageModel model = new MemoryUsageModel();
		MemoryUsage memoryUsage = getMemoryPoolBeanUsage(model,"Old");
		UpdateModelFromMemoryUsage(model, memoryUsage);
		
		return model;
	}
	
	@Http("/PSPermGenMemoryUsage")
	public void getPSPermGenMemoryUsage(final HttpServletResponse resp) throws IOException {
		MemoryUsageModel model = getPSPermGenMemoryUsage();
		
		resp.getWriter().append(JsonUtils.objectToJson(model));
	}
	
	public MemoryUsageModel getPSPermGenMemoryUsage() {
		MemoryUsageModel model = new MemoryUsageModel();
		MemoryUsage memoryUsage = getMemoryPoolBeanUsage(model,"Perm");
		
		// jdk1.8 由Perm改成了Metaspace
		if(memoryUsage == null) {
			memoryUsage = getMemoryPoolBeanUsage(model,"Metaspace");
		}
		UpdateModelFromMemoryUsage(model, memoryUsage);
		
		return model;
	}
	
	private MemoryUsage getMemoryPoolBeanUsage(MemoryUsageModel model, String name) {
		for(MemoryPoolMXBean bean : mpBeanList) {
			if (bean.getName().contains(name)) {
				model.setName(bean.getName());
				return bean.getUsage();
			}
		}
		
		return null;
	}
	
	private void UpdateModelFromMemoryUsage(MemoryUsageModel model, MemoryUsage memoryUsage) {
		model.setInit(memoryUsage.getInit());
		model.setCommited(memoryUsage.getCommitted());
		model.setUsed(memoryUsage.getUsed());
		model.setMax(memoryUsage.getMax());
	}
	
}
