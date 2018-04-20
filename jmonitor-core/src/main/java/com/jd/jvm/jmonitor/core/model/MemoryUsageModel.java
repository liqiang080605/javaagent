package com.jd.jvm.jmonitor.core.model;

public class MemoryUsageModel {

	String name;
	long init;
	long commited;
	long used;
	long max;
	public String getName() {
		return name;
	}
	public long getInit() {
		return init;
	}
	public long getCommited() {
		return commited;
	}
	public long getUsed() {
		return used;
	}
	public long getMax() {
		return max;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setInit(long init) {
		this.init = init;
	}
	public void setCommited(long commited) {
		this.commited = commited;
	}
	public void setUsed(long used) {
		this.used = used;
	}
	public void setMax(long max) {
		this.max = max;
	}
}
