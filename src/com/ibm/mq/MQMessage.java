package com.ibm.mq;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

public class MQMessage extends MQMD {
	
	private static final Charset CHARSET = Charset.defaultCharset(); 
	
	public MQMessage(long offset, int partition) {
		byte[] bOffset = String.valueOf(offset).getBytes(CHARSET);
		byte[] bPartition = String.valueOf(partition).getBytes(CHARSET);
		System.arraycopy(bPartition, 0, this.messageId, 0, bPartition.length);
		System.arraycopy(bOffset, 0, this.correlationId, 0, bOffset.length);
	}
	
	public int getMessageLength() throws IOException {
		int len = 0;
		if (this.messageId != null) {
			len = len + this.messageId.length;
		}
		if (this.correlationId != null) {
			len = len + this.correlationId.length;
		}
		return len;
	}
	
	public int getDataOffset() throws IOException {
		return 4;
	}
	
	public void seek(int offset) throws EOFException {
		
	}
}
