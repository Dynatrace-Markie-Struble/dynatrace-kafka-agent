package com.ibm.mq;

public class MQQueue extends MQManagedObject {
	
	public MQQueue(String name, MQQueueManager connectionReference) {
		super(name, connectionReference);
	}

	public synchronized void put(MQMessage message) throws MQException {
	}
	
	public synchronized void get(MQMessage message) throws MQException {
	}
}
