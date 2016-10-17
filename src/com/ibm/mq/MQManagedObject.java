package com.ibm.mq;

public class MQManagedObject {

	public final String name;
	public final MQQueueManager connectionReference;
	
	public MQManagedObject(String name, MQQueueManager connectionReference) {
		this.name = name;
		this.connectionReference = connectionReference;
	}
	
	public synchronized void close() {
		System.out.println(getClass().getName());
	}
	
	public MQQueueManager getDynatraceConnectionReference() {
		return connectionReference;
	}
	
	public MQQueueManager getConnectionReference() {
		return connectionReference;
	}
	
	public String getName() {
		return name;
	}
}
