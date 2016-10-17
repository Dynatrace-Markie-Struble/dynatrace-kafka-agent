package com.ibm.mq;

public class MQDestination extends MQManagedObject {
	
	public MQDestination(String name, MQQueueManager connectionReference) {
		super(name, connectionReference);
	}

	public synchronized void put(MQMessage message) throws MQException {
	}
	
	public synchronized void get(MQMessage message) throws MQException {
		System.out.println("MQDestination.get(" + message + ")");
	}
	

}
