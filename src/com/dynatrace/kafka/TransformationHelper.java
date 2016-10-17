package com.dynatrace.kafka;

import java.io.InputStream;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * Base class for any class that requires loading of Stub classes located within this
 * Agent Library JAR file.<br />
 * <br /> 
 * Stub Classes contain methods that are holding the enriched method body for classes
 * which are supposed to get transformed. 
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class TransformationHelper {

	public static CtClass loadStubClass(ClassPool classPool, String name) {
		try (
			InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
		) {
			return classPool.makeClass(in);
		} catch (Throwable t) {
			Logging.error("Loading of stub class '" + name + "' failed", t);
			return null;
		}
	}

}
