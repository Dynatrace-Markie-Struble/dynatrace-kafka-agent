package com.dynatrace.kafka;

public class CustomTagGen {

	public static byte[] gen(String topic, int partition, long offset) {
		String sTag = "" + topic + "-" + partition + "-" + offset;
		System.out.println(sTag);
		return sTag.getBytes();
	}
}
