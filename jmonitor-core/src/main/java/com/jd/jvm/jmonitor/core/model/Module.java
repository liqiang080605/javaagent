package com.jd.jvm.jmonitor.core.model;

public class Module {
	private ClassLoader classLoader;
	
	private Object obj;
	
	private Class<?> clazz;

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public Object getObj() {
		return obj;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
}
