package com.jd.jvm.jmonitor.core.model;

public class GcModel {
	String name;
	long collectionCount;
	long collectionTime;
	public String getName() {
		return name;
	}
	public long getCollectionCount() {
		return collectionCount;
	}
	public long getCollectionTime() {
		return collectionTime;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCollectionCount(long collectionCount) {
		this.collectionCount = collectionCount;
	}
	public void setCollectionTime(long collectionTime) {
		this.collectionTime = collectionTime;
	}
	
	
}
