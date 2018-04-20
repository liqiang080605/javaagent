package com.jd.jvm.jmonitor.core.model;

public class TomcatThreadModel {
	String name;
	int maxThreads;
	int connectionCount;
	int currentThreadCount;
	int maxConnections;
	int maxKeepAliveRequests;
	public String getName() {
		return name;
	}
	public int getMaxThreads() {
		return maxThreads;
	}
	public int getConnectionCount() {
		return connectionCount;
	}
	public int getCurrentThreadCount() {
		return currentThreadCount;
	}
	public int getMaxConnections() {
		return maxConnections;
	}
	public int getMaxKeepAliveRequests() {
		return maxKeepAliveRequests;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}
	public void setConnectionCount(int connectionCount) {
		this.connectionCount = connectionCount;
	}
	public void setCurrentThreadCount(int currentThreadCount) {
		this.currentThreadCount = currentThreadCount;
	}
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	public void setMaxKeepAliveRequests(int maxKeepAliveRequests) {
		this.maxKeepAliveRequests = maxKeepAliveRequests;
	}
	
}
