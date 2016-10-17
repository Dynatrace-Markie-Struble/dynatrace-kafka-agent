package com.dynatrace.kafka;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.ClassClassPath;
import javassist.ClassPool;

/**
 * A Java Agent for modifying Byte Code within WebSphere Liberty Profile
 * for Comerica.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class KafkaJavaAgent extends TransformationHelper implements ClassFileTransformer {
	
	/** printing out installation instructions **/
	public static void main(String[] args) {
		System.out.println("Installation Instructions:");
		System.out.println("  * Stop your WLP Server Instance");
		System.out.println("  * Copy this file onto the host where WLP is getting launched into a folder of your choice");
		System.out.println("    - Later on this folder will be referenced as <AGENT_HOME>");
		System.out.println("    - Save yourself the hassle and choose a folder/path without white spaces");
		System.out.println("  * Ensure that the user running WLP is allowed to READ dynatrace-comerica-agent.jar file");
		System.out.println("  * Add the following line to file <WLP_HOME>/wlp/usr/servers/<SERVER_NAME>/jvm.options");
		System.out.println("    (If that file doesn't exist, you need to create it)");
		System.out.println("    (<WLP_HOME> is the installation folder of your WebSphere Liberty Profile Server)");
		System.out.println("    (<SERVER_NAME> is the sub folder of the Server Instance you want to add this Java Agent to)");
		System.out.println("      -javaagent:<AGENT_HOME>/dynatrace-comerica-agent.jar");
		System.out.println("  * In case jvm.options already exists and contains the -agentpath JVM argument for the Dynatrace Agent, make sure that -javaagent is listed BEFORE -agentpath");
		System.out.println("  * Start your WLP Server Instance");
		System.out.println();
		System.out.println("Troubleshooting (enable log output to stdout):");
		System.out.println("  * Turn on all log output by adding -Dcom.dynatrace.comerica.agent.loglevel=FINE to <WLP_HOME>/wlp/usr/servers/<SERVER_NAME>/jvm.options");
		System.out.println("  * Reduce log output to Class Transformation, Warnings and Errors by adding -Dcom.dynatrace.comerica.agent.loglevel=INFO to <WLP_HOME>/wlp/usr/servers/<SERVER_NAME>/jvm.options");
		System.out.println("  * Reduce log output to Warnings and Errors by adding -Dcom.dynatrace.comerica.agent.loglevel=WARNING to <WLP_HOME>/wlp/usr/servers/<SERVER_NAME>/jvm.options (default setting)");
		System.out.println("  * Reduce log output to Errors by adding -Dcom.dynatrace.comerica.agent.loglevel=ERROR to <WLP_HOME>/wlp/usr/servers/<SERVER_NAME>/jvm.options");
		System.out.println("  * Turn on all log output by adding -Dcom.dynatrace.comerica.agent.loglevel=NONE to <WLP_HOME>/wlp/usr/servers/<SERVER_NAME>/jvm.options");
		System.out.println();
		System.out.println("Development:");
		System.out.println("  * This JAR file contains all required source files for modifying it further");
		System.out.println("  * Just extract the JAR file into a folder");
		System.out.println("  * Import the folder as Eclipse Project into Eclipse Mars.1 (or later)");
		System.out.println("  * Create a modified JAR file via ANT using the included build.xml");
	}

	/**
	 * Called by the JVM - we are registering an instance of {@link KafkaJavaAgent} as {@link ClassFileTransformer} here.
	 */
    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            inst.addTransformer(new KafkaJavaAgent(), true);
        } catch (Throwable t) {
        	Logging.error(null, t);
        }
    }

    /**
     * The classes we are potentially transforming here are
     * <ul>
     * <li>org/eclipse/osgi/internal/loader/EquinoxClassLoader</li>
     * <li>org/eclipse/osgi/internal/loader/ModuleClassLoader</li>
     * <li>com/comerica/pci/isoconverter/integration/IsoRestClient</li>
     * </ul>
     * It might be of interest to also add {@code com.ibm.ws.webcontainer.servlet.ServletWrapper} here but
     * this Agent actually was NECESSARY because a Class File Transformer somehow is never getting passed
     * the byte code of that class. Therefore until further notice not necessary.<br />
     * <br />
     * {@code com/comerica/pci/isoconverter/integration/IsoRestClient} on the other hand apparently IS
     * getting handed over to this Class Transformer (although it also packaged up inside an EAR). 
     */
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] buffer) throws IllegalClassFormatException {
		if (className.equals("com/comerica/pci/isoconverter/integration/IsoRestClient")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzServletConfig = loader.loadClass("com.sun.jersey.api.client.ClientResponse");
				classPool.insertClassPath(new ClassClassPath(clazzServletConfig));
				buffer = EquinoxClassLoaderHelper.transformIsoRestCient(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of com.ibm.ws.webcontainer.servlet.ServletWrapper failed", t);
			}
//		} else if (className.equals("org/apache/kafka/common/record/Record")) {
//			try {
//				final ClassPool classPool = ClassPool.getDefault();
//				
//				Class<?> clazzCrc32 = loader.loadClass("kafka.message.Message");
//				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
//				buffer = EquinoxClassLoaderHelper.transformKafkaRecord(classPool, buffer);
//			} catch (Throwable t) {
//				Logging.error("interception of loading of com.ibm.ws.webcontainer.servlet.ServletWrapper failed", t);
//			} 
		} else if (className.equals("kafka/message/MessageAndOffset")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzCrc32 = loader.loadClass("kafka.message.Message");
				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
				buffer = EquinoxClassLoaderHelper.transformMessageAndOffset(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of kafka.message.MessageAndOffset failed", t);
			}
		} else if (className.equals("org/apache/kafka/clients/producer/KafkaProducer")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzCrc32 = loader.loadClass("org.apache.kafka.clients.producer.Callback");
				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
				buffer = EquinoxClassLoaderHelper.transformKafkaProducer(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of org.apache.kafka.clients.producer.KafkaProducer failed", t);
			}
		} else if (className.equals("kafka/consumer/ConsumerIterator")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzCrc32 = loader.loadClass("kafka.consumer.Consumer");
				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
				buffer = EquinoxClassLoaderHelper.transformConsumerIterator(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of kafka.consumer.ConsumerIterator", t);
			}
		} else if (className.equals("kafka/message/ByteBufferMessageSet")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzCrc32 = loader.loadClass("kafka.message.MessageSet");
				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
				buffer = EquinoxClassLoaderHelper.transformByteBufferMessageSet(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of kafka.message.MessageAndOffset failed", t);
			}
		} else if (className.equals("kafka/api/TopicData")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzCrc32 = loader.loadClass("kafka.api.Request");
				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
				buffer = EquinoxClassLoaderHelper.transformTopicData(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of kafka.api.TopicData failed", t);
			}
		} else if (className.equals("kafka/api/FetchResponsePartitionData")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzCrc32 = loader.loadClass("kafka.api.Request");
				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
				buffer = EquinoxClassLoaderHelper.transformFetchResponsePartitionData(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of kafka.api.FetchResponsePartitionData failed", t);
			}
		} else if (className.equals("org/apache/kafka/clients/consumer/ConsumerRecords$ConcatenatedIterable$1")) {
			try {
				final ClassPool classPool = ClassPool.getDefault();
				
				Class<?> clazzCrc32 = loader.loadClass("org.apache.kafka.clients.consumer.ConsumerRecord");
				classPool.insertClassPath(new ClassClassPath(clazzCrc32));
				buffer = EquinoxClassLoaderHelper.transformConsumerRecordIterator(classPool, buffer);
			} catch (Throwable t) {
				Logging.error("interception of loading of kafka.api.FetchResponsePartitionData failed", t);
			}
		}
		return buffer;
	}

}
