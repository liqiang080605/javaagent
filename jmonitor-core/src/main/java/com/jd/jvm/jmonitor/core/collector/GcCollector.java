package com.jd.jvm.jmonitor.core.collector;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.model.GcModel;
import com.jd.jvm.jmonitor.core.util.JsonUtils;

@Http("gc")
public class GcCollector {
	private List<GarbageCollectorMXBean> gcBeanList = ManagementFactory.getGarbageCollectorMXBeans();

	@Http("/gcCollect")
	public void getGcCollect(final HttpServletResponse resp) throws IOException {
		
		List<GcModel> list = getGcCollect();
		resp.getWriter().append(JsonUtils.objectToJson(list));
	}
	
	public List<GcModel> getGcCollect() {
		List<GcModel> list = new ArrayList<GcModel>();
		for(GarbageCollectorMXBean bean : gcBeanList) {
			GcModel model = new GcModel();
			
			if(bean.getName().contains("ParNew")) {
				model.setName("PS Scavenge");
			} else if (bean.getName().contains("MarkSweep")) {
				model.setName("PS MarkSweep");
			} else {
				model.setName(bean.getName());
			}
			
			model.setCollectionCount(bean.getCollectionCount());
			model.setCollectionTime(bean.getCollectionTime());
			
			list.add(model);
		}
		return list;
	}
	
}
