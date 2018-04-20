package com.jd.jvm.jmonitor.core.model;

public class MetricModel {
	String name;
	String cName;
	String url;
	String parentUrl;
	String parentName;
	String type;
	String handleClassName;
	String handleMethodName;
	
	public String getName() {
		return name;
	}
	public String getcName() {
		return cName;
	}
	public String getUrl() {
		return url;
	}
	public String getParentUrl() {
		return parentUrl;
	}
	public String getParentName() {
		return parentName;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setcName(String cName) {
		this.cName = cName;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setParentUrl(String parentUrl) {
		this.parentUrl = parentUrl;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getHandleClassName() {
		return handleClassName;
	}
	public String getHandleMethodName() {
		return handleMethodName;
	}
	public void setHandleClassName(String handleClassName) {
		this.handleClassName = handleClassName;
	}
	public void setHandleMethodName(String handleMethodName) {
		this.handleMethodName = handleMethodName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
