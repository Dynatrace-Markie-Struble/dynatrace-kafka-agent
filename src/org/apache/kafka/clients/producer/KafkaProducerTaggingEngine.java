package org.apache.kafka.clients.producer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import com.ibm.mq.MQDestination;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;

public class KafkaProducerTaggingEngine extends Thread {
	
	static {
		DynaTraceADKFactory.initialize();
	}
	
	private static class TaggedFuture {
		
		public final Future<RecordMetadata> future;
		public final byte[] traceTag;
		
		public TaggedFuture(Future<RecordMetadata> future) {
			this.future = future;
			this.traceTag = resolveTraceTag();
		}
		
		private byte[] resolveTraceTag() {
			Tagging tagging = DynaTraceADKFactory.createTagging();
			if (tagging == null) {
				return null;
			}
			byte[] currentTag = tagging.getTag();
			if (!tagging.isTagValid(currentTag)) {
				return null;
			}
			tagging.linkClientPurePath(true);
			return currentTag;
		}
	}
	
	public static final KafkaProducerTaggingEngine INSTANCE = create();
	
	private final BlockingQueue<TaggedFuture> QUEUE = new LinkedBlockingQueue<>();
	
	private static KafkaProducerTaggingEngine create() {
		KafkaProducerTaggingEngine engine = new KafkaProducerTaggingEngine();
		engine.start();
		
		return engine;
	}
	
	private KafkaProducerTaggingEngine() {
		setDaemon(true);
	}
	
	public static Future<RecordMetadata> handle(Future<RecordMetadata> future) {
		INSTANCE.QUEUE.add(new TaggedFuture(future));
		return future;
	}
	
	public void sendMQMessage(String topic, int partition, long offset) {
		MQMessage mqMessage = new MQMessage(offset, partition);
		MQDestination mqQueue = new MQDestination(topic, MQQueueManager.INSTANCE);
		try {
			mqQueue.put(mqMessage);
		} catch (MQException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				TaggedFuture taggedFuture = QUEUE.take();
				if (taggedFuture == null) {
					continue;
				}
				Tagging tagging = DynaTraceADKFactory.createTagging();
				if (tagging == null) {
					continue;
				}
				if (!tagging.isTagValid(taggedFuture.traceTag)) {
					continue;
				}
				tagging.setTag(taggedFuture.traceTag);
				tagging.startServerPurePath();
				Future<RecordMetadata> future = taggedFuture.future;
				RecordMetadata recordMetadata = future.get();
				if (recordMetadata == null) {
					return;
				}
				long offset = recordMetadata.offset();
				String topic = recordMetadata.topic();
				int partition = recordMetadata.partition();
				
				sendMQMessage(topic, partition, offset);
				
//				CustomTag customTag = tagging.createCustomTag(CustomTagGen.gen(topic, partition, offset));
//				tagging.linkClientPurePath(true, customTag);

				
				tagging.endServerPurePath();
			} catch (InterruptedException e) {
				return;
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
