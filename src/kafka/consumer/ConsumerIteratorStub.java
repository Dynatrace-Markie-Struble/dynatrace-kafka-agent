package kafka.consumer;

import kafka.message.MessageAndMetadata;

public class ConsumerIteratorStub {
	
	public MessageAndMetadata next_dtd_orig() {
		return null;
	}
	
	public MessageAndMetadata next() {
		return PurePathStarter.handle(next_dtd_orig());
	}
}
