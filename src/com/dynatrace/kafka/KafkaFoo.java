package com.dynatrace.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

public class KafkaFoo {

	public static void main(String[] args) {
		String payload = UUID.randomUUID().toString();
		byte[] payloadBytes = payload.getBytes(Charset.defaultCharset());
		String traceTag = "FW;1481367847;0;4;1481367847;0;0";
		byte[] traceTagBytes = traceTag.getBytes(Charset.defaultCharset());
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			baos.write(payloadBytes);
			baos.write(traceTagBytes);
			byte[] taggedPayloadBytes = baos.toByteArray();
			String resolvedPayload = new String(taggedPayloadBytes, Charset.defaultCharset());
			System.out.println(resolvedPayload);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
