package org.apache.kafka.clients.consumer;

public class ConsumerRecord<K, V> {

	public long offset() {
		return 0;
	}
	
	public int partition() {
		return 0;
	}
	
	public String topic() {
		return null;
	}
}
