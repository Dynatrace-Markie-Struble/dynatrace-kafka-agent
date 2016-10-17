package org.apache.kafka.clients.producer;

import java.util.concurrent.Future;

public class KafkaProducerStub {
	
	public Future<RecordMetadata> send_dtd_orig(ProducerRecord record, Callback callback) {
		return null;
	}

	
	public Future<RecordMetadata> send(ProducerRecord record, Callback callback) {
		return KafkaProducerTaggingEngine.handle(send_dtd_orig(record, callback));
	}
}
