package com.dynatrace.kafka;

import java.io.ByteArrayInputStream;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

/**
 * Transformation Helper for modifying the byte code of classes
 * {@code com.ibm.ws.webcontainer.servlet.ServletWrapper} and
 * {@code com.comerica.pci.isoconverter.integration.IsoRestClient}.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class EquinoxClassLoaderHelper extends TransformationHelper {

	/**
	 * Modifies the byte code of class {@code com.ibm.ws.webcontainer.servlet.ServletWrapper}. <br />
	 * <br />
	 * Method {@code service} will get enriched with Dynatrace ADK code and eventually will call the newly introduced
	 * method {@code service_orig}, which holds the byte code of the original method {@code service}.<br />
	 * 
	 * @param classPool the Java Assist Class Pool to use
	 * @param buffer the byte code of the class
	 * 
	 * @return the modified byte code
	 */
	public static byte[] transformServletWrapper(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of com.ibm.ws.webcontainer.servlet.ServletWrapper");
		try {
			// loading the stub class holding modified byte code for us
			CtClass ctServletWrapperStub = loadStubClass(classPool, "com/ibm/ws/webcontainer/servlet/ServletWrapperStub.class");
			// loading the class in memory for modification
			CtClass ctServletWrapper = classPool.makeClass(new ByteArrayInputStream(buffer));
			CtMethod[] ctServiceOrigMethods = ctServletWrapper.getDeclaredMethods("service");
			for (CtMethod ctServiceOrigMethod : ctServiceOrigMethods) {
				// just a sanity check in case this class "somehow" comes up with multiple "service" methods
				if (ctServiceOrigMethod.getParameterTypes().length == 3) {
					// renaming the existing method "service" to "service_orig"
					ctServiceOrigMethod.setName("service_orig");
					// create a new method "service" with the same signature than the original "service" method
					CtMethod ctServiceMethod = new CtMethod(ctServiceOrigMethod.getReturnType(), "service", ctServiceOrigMethod.getParameterTypes(), ctServletWrapper);
					ctServiceMethod.setExceptionTypes(ctServiceOrigMethod.getExceptionTypes());
					ctServiceMethod.setModifiers(ctServiceOrigMethod.getModifiers());
					// using the method body of the previously loaded stub class as method body
					ctServiceMethod.setBody(ctServletWrapperStub.getDeclaredMethod("service"), null);
					
					Logging.info("  wrapping method service");
					
					// re-adding method "service" to our resulting class - the one that existed up to now has been renamed to "service_orig" already.
					ctServletWrapper.addMethod(ctServiceMethod);
					
					// in memory compilation
					ctServletWrapper.rebuildClassFile();
					
					// returning modified byte code
					return ctServletWrapper.toBytecode();
				}
			}
			return buffer;
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of com.ibm.ws.webcontainer.servlet.ServletWrapper failed", t);
			return buffer;
		}
	}
	
	/**
	 * Modifies the byte code of class {@code com.comerica.pci.isoconverter.integration.IsoRestClient}. <br />
	 * <br />
	 * Method {@code post} will get enriched with Dynatrace ADK code and eventually will call the newly introduced
	 * method {@code post_orig}, which holds the byte code of the original method {@code post}.<br />
	 * 
	 * @param classPool the Java Assist Class Pool to use
	 * @param buffer the byte code of the class
	 * 
	 * @return the modified byte code
	 */
	public static byte[] transformIsoRestCient(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of com.comerica.pci.isoconverter.integration.IsoRestClient");
		try {
			// loading the stub class holding modified byte code for us
			CtClass ctIsoRestClientStub = loadStubClass(classPool, "com/comerica/pci/isoconverter/integration/IsoRestClientStub.class");
			// loading the class in memory for modification
			CtClass ctIsoRestClient = classPool.makeClass(new ByteArrayInputStream(buffer));
			CtMethod[] ctPostOrigMethods = ctIsoRestClient.getDeclaredMethods("post");
			for (CtMethod ctPostOrigMethod : ctPostOrigMethods) {
				// just a sanity check in case this class "post" comes up with multiple "post" methods
				if (ctPostOrigMethod.getParameterTypes().length == 5) {
					// renaming the existing method "post" to "post_orig"
					ctPostOrigMethod.setName("post_orig");
					// create a new method "post" with the same signature than the original "post" method
					CtMethod ctPostMethod = new CtMethod(ctPostOrigMethod.getReturnType(), "post", ctPostOrigMethod.getParameterTypes(), ctIsoRestClient);
					ctPostMethod.setExceptionTypes(ctPostOrigMethod.getExceptionTypes());
					ctPostMethod.setModifiers(ctPostOrigMethod.getModifiers());
					
					// using the method body of the previously loaded stub class as method body
					ctPostMethod.setBody(ctIsoRestClientStub.getDeclaredMethod("post"), null);
					
					Logging.info("  wrapping method post");
					
					// re-adding method "post" to our resulting class - the one that existed up to now has been renamed to "post_orig" already.
					ctIsoRestClient.addMethod(ctPostMethod);
					// in memory compilation
					ctIsoRestClient.rebuildClassFile();
					// returning modified byte code
					return ctIsoRestClient.toBytecode();
				}
			}
			return buffer;
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of com.comerica.pci.isoconverter.integration.IsoRestClient failed", t);
			return buffer;
		}
	}  
	
	// 
	
	public static byte[] transformKafkaProducer(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of org.apache.kafka.clients.producer.KafkaProducer");
		try {
			// loading the stub class holding modified byte code for us
			CtClass ctKafkaProducerStub = loadStubClass(classPool, "org/apache/kafka/clients/producer/KafkaProducerStub.class");
			// loading the class in memory for modification
			CtClass ctKafkaProducer = classPool.makeClass(new ByteArrayInputStream(buffer));
			CtMethod[] ctSendOrigMethods = ctKafkaProducer.getDeclaredMethods("send");
			for (CtMethod ctSendOrigMethod : ctSendOrigMethods) {
				// just a sanity check in case this class "post" comes up with multiple "send" methods
				if (ctSendOrigMethod.getParameterTypes().length == 2) {
					// renaming the existing method "send" to "send_dtd_orig"
					ctSendOrigMethod.setName("send_dtd_orig");
					// create a new method "send" with the same signature than the original "send" method
					CtMethod ctSendMethod = new CtMethod(ctSendOrigMethod.getReturnType(), "send", ctSendOrigMethod.getParameterTypes(), ctKafkaProducer);
					ctSendMethod.setExceptionTypes(ctSendOrigMethod.getExceptionTypes());
					ctSendMethod.setModifiers(ctSendOrigMethod.getModifiers());
					
					// using the method body of the previously loaded stub class as method body
					ctSendMethod.setBody(ctKafkaProducerStub.getDeclaredMethod("send"), null);
					
					Logging.info("  wrapping method send");
					
					// re-adding method "post" to our resulting class - the one that existed up to now has been renamed to "post_orig" already.
					ctKafkaProducer.addMethod(ctSendMethod);
					// in memory compilation
					ctKafkaProducer.rebuildClassFile();
					// returning modified byte code
					return ctKafkaProducer.toBytecode();
				}
			}
			return buffer;
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of org.apache.kafka.clients.producer.KafkaProducer failed", t);
			return buffer;
		}
	} 
	
	public static byte[] transformConsumerIterator(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of kafka.consumer.ConsumerIterator");
		try {
			// loading the stub class holding modified byte code for us
			CtClass ctConsumerIteratorStub = loadStubClass(classPool, "kafka/consumer/ConsumerIteratorStub.class");
			// loading the class in memory for modification
			CtClass ctConsumerIterator = classPool.makeClass(new ByteArrayInputStream(buffer));
			CtMethod[] ctNextOrigMethods = ctConsumerIterator.getDeclaredMethods("next");
			for (CtMethod ctNextOrigMethod : ctNextOrigMethods) {
				// just a sanity check in case this class "next" comes up with multiple "next" methods
				if (ctNextOrigMethod.getParameterTypes().length == 0) {
					// renaming the existing method "next" to "next_dtd_orig"
					ctNextOrigMethod.setName("next_dtd_orig");
					// create a new method "send" with the same signature than the original "send" method
					CtMethod ctNextMethod = new CtMethod(ctNextOrigMethod.getReturnType(), "next", ctNextOrigMethod.getParameterTypes(), ctConsumerIterator);
					ctNextMethod.setExceptionTypes(ctNextOrigMethod.getExceptionTypes());
					ctNextMethod.setModifiers(ctNextOrigMethod.getModifiers());
					
					// using the method body of the previously loaded stub class as method body
					ctNextMethod.setBody(ctConsumerIteratorStub.getDeclaredMethod("next"), null);
					
					Logging.info("  wrapping method next");
					
					// re-adding method "post" to our resulting class - the one that existed up to now has been renamed to "post_orig" already.
					ctConsumerIterator.addMethod(ctNextMethod);
					// in memory compilation
					ctConsumerIterator.rebuildClassFile();
					// returning modified byte code
					return ctConsumerIterator.toBytecode();
				}
			}
			return buffer;
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of org.apache.kafka.clients.producer.KafkaProducer failed", t);
			return buffer;
		}
	}  
	
	public static byte[] transformKafkaRecord(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of org.apache.kafka.common.record.Record");
		try {
			// loading the stub class holding modified byte code for us
			CtClass ctRecordStub = loadStubClass(classPool, "org/apache/kafka/common/record/RecordStub.class");
			// loading the class in memory for modification
			CtClass ctRecord = classPool.makeClass(new ByteArrayInputStream(buffer));
			CtMethod[] ctRecordStubMethods = ctRecordStub.getDeclaredMethods();
			for (CtMethod ctRecordStubMethod : ctRecordStubMethods) {
				if (ctRecordStubMethod == null) {
					continue;
				}
				if ("creationTimestamp".equals(ctRecordStubMethod.getName())) {
					CtMethod ctCreationTimestampMethod = new CtMethod(ctRecordStubMethod.getReturnType(), "creationTimestamp", ctRecordStubMethod.getParameterTypes(), ctRecord);
					ctCreationTimestampMethod.setExceptionTypes(ctRecordStubMethod.getExceptionTypes());
					ctCreationTimestampMethod.setModifiers(ctRecordStubMethod.getModifiers());
					ctCreationTimestampMethod.setBody(ctRecordStubMethod, null);
					ctRecord.addMethod(ctCreationTimestampMethod);
					// in memory compilation
					ctRecord.rebuildClassFile();
					// returning modified byte code
					return ctRecord.toBytecode();
					
				}
			}
			return buffer;
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of org.apache.kafka.common.record.Record failed", t);
			return buffer;
		}
	}
	
	
	public static byte[] transformMessageAndOffset(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of kafka.message.MessageAndOffset");
		try {
//			// loading the stub class holding modified byte code for us
//			CtClass ctRecordStub = loadStubClass(classPool, "org/apache/kafka/common/record/RecordStub.class");
			// loading the class in memory for modification
			CtClass ctMessageAndOffset = classPool.makeClass(new ByteArrayInputStream(buffer));
			
			CtConstructor ctMessageAndOffsetCtor = ctMessageAndOffset.getDeclaredConstructor(new CtClass[] { classPool.getCtClass("kafka.message.Message"), CtClass.longType });
			// ctMessageAndOffsetCtor.insertAfter("try { java.lang.System.out.println(new StringBuilder(\"offset: \").append(offset).toString()); throw new com.dynatrace.kafka.DebugException(); } catch (Throwable t) { }");
			ctMessageAndOffsetCtor.insertAfter("try { throw new com.dynatrace.kafka.DebugException(); } catch (Throwable t) { }");
			
			ctMessageAndOffset.rebuildClassFile();
			return ctMessageAndOffset.toBytecode();
			
//			CtMethod[] ctRecordStubMethods = ctRecordStub.getDeclaredMethods();
//			for (CtMethod ctRecordStubMethod : ctRecordStubMethods) {
//				if (ctRecordStubMethod == null) {
//					continue;
//				}
//				if ("creationTimestamp".equals(ctRecordStubMethod.getName())) {
//					CtMethod ctCreationTimestampMethod = new CtMethod(ctRecordStubMethod.getReturnType(), "creationTimestamp", ctRecordStubMethod.getParameterTypes(), ctMessageAndOffset);
//					ctCreationTimestampMethod.setExceptionTypes(ctRecordStubMethod.getExceptionTypes());
//					ctCreationTimestampMethod.setModifiers(ctRecordStubMethod.getModifiers());
//					ctCreationTimestampMethod.setBody(ctRecordStubMethod, null);
//					ctMessageAndOffset.addMethod(ctCreationTimestampMethod);
//					// in memory compilation
//					ctMessageAndOffset.rebuildClassFile();
//					// returning modified byte code
//					return ctMessageAndOffset.toBytecode();
//					
//				}
//			}
//			return buffer;
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of kafka.message.MessageAndOffset failed", t);
			return buffer;
		}
	}
	
	public static byte[] transformByteBufferMessageSet(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of kafka.message.ByteBufferMessageSet");
		try {
			CtClass ctByteBufferMessageSet = classPool.makeClass(new ByteArrayInputStream(buffer));
			
			CtConstructor[] ctors = ctByteBufferMessageSet.getDeclaredConstructors();
			if (ctors != null) {
				for (CtConstructor ctor : ctors) {
					Logging.info("   .. enriching constructor");
					ctor.insertAfter("try { throw new com.dynatrace.kafka.DebugException(); } catch (Throwable t) { }");
				}
			}
			ctByteBufferMessageSet.rebuildClassFile();
			return ctByteBufferMessageSet.toBytecode();
			
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of kafka.message.ByteBufferMessageSet failed", t);
			return buffer;
		}
	}		
	
	public static byte[] transformTopicData(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of kafka.api.TopicData");
		try {
			CtClass ctByteBufferMessageSet = classPool.makeClass(new ByteArrayInputStream(buffer));
			
			CtConstructor[] ctors = ctByteBufferMessageSet.getDeclaredConstructors();
			if (ctors != null) {
				for (CtConstructor ctor : ctors) {
					Logging.info("   .. enriching constructor");
					// ctor.insertAfter("try { java.lang.System.out.println(new StringBuilder(\"partitionData: \").append(partitionData.keySet().size()).toString()); java.lang.System.out.println(new StringBuilder(\"topic: \").append(topic).toString()); throw new com.dynatrace.kafka.DebugException(); } catch (Throwable t) {  }");
					ctor.insertAfter("try { throw new com.dynatrace.kafka.DebugException(); } catch (Throwable t) {  }");
				}
			}
			ctByteBufferMessageSet.rebuildClassFile();
			return ctByteBufferMessageSet.toBytecode();
			
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of kafka.api.TopicData failed", t);
			return buffer;
		}
	}	
	
	public static byte[] transformFetchResponsePartitionData(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of kafka.api.FetchResponsePartitionData");
		try {
			CtClass ctFetchResponsePartitionData = classPool.makeClass(new ByteArrayInputStream(buffer));
			ctFetchResponsePartitionData.addField(new CtField(CtClass.intType, "dtdPartitionId", ctFetchResponsePartitionData));
			
			CtConstructor[] ctors = ctFetchResponsePartitionData.getDeclaredConstructors();
			if (ctors != null) {
				for (CtConstructor ctor : ctors) {
					Logging.info("   .. enriching constructor");
					ctor.insertAfter("try { throw new com.dynatrace.kafka.DebugException(); } catch (Throwable t) {  }");
				}
			}
			ctFetchResponsePartitionData.rebuildClassFile();
			return ctFetchResponsePartitionData.toBytecode();
			
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of kafka.api.FetchResponsePartitionData failed", t);
			return buffer;
		}
	}		
	
	public static byte[] transformConsumerRecordIterator(ClassPool classPool, byte[] buffer) {
		Logging.info("intercepted loading of org.apache.kafka.clients.consumer.ConsumerRecords$ConcatenatedIterable$1");
		try {
			// loading the stub class holding modified byte code for us
			CtClass ctConsumerIteratorStub = loadStubClass(classPool, "org/apache/kafka/clients/consumer/ConsumerRecordsDollarConcatenatedIterableDollar1Stub.class");
			// loading the class in memory for modification
			CtClass ctConsumerIterator = classPool.makeClass(new ByteArrayInputStream(buffer));
			CtMethod[] ctNextOrigMethods = ctConsumerIterator.getDeclaredMethods("makeNext");
			for (CtMethod ctNextOrigMethod : ctNextOrigMethods) {
				// just a sanity check in case this class "next" comes up with multiple "next" methods
				if (ctNextOrigMethod.getParameterTypes().length == 0) {
					// renaming the existing method "next" to "next_dtd_orig"
					ctNextOrigMethod.setName("makeNext_dtd_orig");
					// create a new method "send" with the same signature than the original "send" method
					CtMethod ctNextMethod = new CtMethod(ctNextOrigMethod.getReturnType(), "makeNext", ctNextOrigMethod.getParameterTypes(), ctConsumerIterator);
					ctNextMethod.setExceptionTypes(ctNextOrigMethod.getExceptionTypes());
					ctNextMethod.setModifiers(ctNextOrigMethod.getModifiers());
					
					// using the method body of the previously loaded stub class as method body
					ctNextMethod.setBody(ctConsumerIteratorStub.getDeclaredMethod("makeNext"), null);
					
					Logging.info("  wrapping method makeNext");
					
					// re-adding method "post" to our resulting class - the one that existed up to now has been renamed to "post_orig" already.
					ctConsumerIterator.addMethod(ctNextMethod);
					// in memory compilation
					ctConsumerIterator.rebuildClassFile();
					// returning modified byte code
					return ctConsumerIterator.toBytecode();
				}
			}
			return buffer;
		} catch (Throwable t) {
			// even if everything fails we NEED to return byte code - by default the original byte code
			Logging.error("transformation of org.apache.kafka.clients.consumer.ConsumerRecords$ConcatenatedIterable$1 failed", t);
			return buffer;
		}
	}		
	
	
}
