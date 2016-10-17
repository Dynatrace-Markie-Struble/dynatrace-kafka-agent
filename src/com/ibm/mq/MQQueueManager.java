package com.ibm.mq;

public class MQQueueManager extends MQManagedObject {
	
	public static final MQQueueManager INSTANCE = new MQQueueManager("kafka");

	public MQQueueManager(String name) {
		super(name, null);
	}
	
}
