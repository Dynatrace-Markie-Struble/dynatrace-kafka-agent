package kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import com.dynatrace.kafka.CustomTagGen;
import com.ibm.mq.MQDestination;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;

import kafka.message.MessageAndMetadata;

public class PurePathStarter {
	
	static {
		DynaTraceADKFactory.initialize();
	}
	
	public static ConsumerRecord<?, ?> handle(ConsumerRecord<?, ?> record) {
		if (record == null) {
			return record;
		}
		String topic = record.topic();
		MQMessage mqMessage = new MQMessage(record.offset(), record.partition());
		MQDestination mqQueue = new MQDestination(topic, MQQueueManager.INSTANCE);
		try {
			mqQueue.get(mqMessage);
		} catch (MQException e) {
			e.printStackTrace();
		}
		return record;
	}
	
	public static MessageAndMetadata handle(MessageAndMetadata mamd) {
		if (mamd == null) {
			return process(mamd);
		}
		String topic = mamd.topic();
		MQMessage mqMessage = new MQMessage(mamd.offset(), mamd.partition());
		MQDestination mqQueue = new MQDestination(topic, MQQueueManager.INSTANCE);
		try {
			mqQueue.get(mqMessage);
		} catch (MQException e) {
			e.printStackTrace();
		}
		return process(mamd);
	}

	public static MessageAndMetadata handle_(MessageAndMetadata mamd) {
		if (mamd == null) {
			return null;
		}
		Tagging tagging = DynaTraceADKFactory.createTagging();
		if (tagging == null) {
			return mamd;
		}
		byte[] currentTag = tagging.getTag();
		if (tagging.isTagValid(currentTag)) {
			return mamd;
		}
		long offset = mamd.offset();
		int partition = mamd.partition();
		String topic = mamd.topic();
		tagging.setCustomTag(CustomTagGen.gen(topic, partition, offset));
		tagging.startServerPurePath();
		try {
			tagging.linkClientPurePath(true);
			return process(mamd);
		} finally {
			tagging.endServerPurePath();
		}
	}
	
	private static MessageAndMetadata process(MessageAndMetadata t) {
		return t;
	}

}
