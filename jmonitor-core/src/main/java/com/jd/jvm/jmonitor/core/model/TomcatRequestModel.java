package com.jd.jvm.jmonitor.core.model;

public class TomcatRequestModel {
	String name;
	long bytesSent;
	long bytesReceived;
	long processingTime;
	long errorCount;
	long maxTime;
	long requestCount;
	public long getBytesSent() {
		return bytesSent;
	}
	public long getProcessingTime() {
		return processingTime;
	}
	public long getErrorCount() {
		return errorCount;
	}
	public long getMaxTime() {
		return maxTime;
	}
	public void setBytesSent(long bytesSent) {
		this.bytesSent = bytesSent;
	}
	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}
	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}
	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}
	public String getName() {
		return name;
	}
	public long getBytesReceived() {
		return bytesReceived;
	}
	public void setBytesReceived(long bytesReceived) {
		this.bytesReceived = bytesReceived;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getRequestCount() {
		return requestCount;
	}
	public void setRequestCount(long requestCount) {
		this.requestCount = requestCount;
	}
	
}
