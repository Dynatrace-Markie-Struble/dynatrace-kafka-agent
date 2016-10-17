package com.dynatrace.kafka;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Helper class for logging
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class Logging {
	
	/**
	 * System Property for changing the Log {@link Level}.<br />
	 * <br />
	 * Possible values are
	 * <ul>
	 * <li>NONE which prevents any logging at all</li>
	 * <li>INFO which allows all log messages to end up in {@link System#out}</li>
	 * <li>WARNING which allows all log messages for {@link Level#WARNING} and {@link Level#ERROR} to end up in {@link System#out}</li>
	 * <li>ERROR which allows all log messages for {@link Level#ERROR} to end up in {@link System#out}</li>
	 * </ul>
	 */
	private static final String PROPERTY_LOG_LEVEL = "com.dynatrace.comerica.agent.loglevel";
	
	/**
	 * The log {@link Level} logged out messages need to match.
	 */
	private static final Level LEVEL = resolveLogLevel();
	
	private Logging() {
		// prevent instantiation
	}
	
	/**
	 * Resolves the Log {@link Level} from System Property. Falls back to {@link Level#INFO}.
	 * 
	 * @return the resolved {@link Level}
	 */
	private static Level resolveLogLevel() {
		String sLogLevel = System.getProperty(PROPERTY_LOG_LEVEL);
		if (sLogLevel == null) {
			sLogLevel = Level.WARNING.name();
		}
		sLogLevel = sLogLevel.toUpperCase();
		try {
			return Level.valueOf(sLogLevel);
		} catch (Throwable t) {
			System.err.println("Invalid value '" + sLogLevel + "' for System Property '" + PROPERTY_LOG_LEVEL + "' - falling back to '" + Level.WARNING + "'");
			return Level.WARNING;
		}
	}
	
	/**
	 * Possible Log {@link Level}s
	 * 
	 * @author reinhard.pilz@dynatrace.com
	 *
	 */
	private static enum Level {
		NONE(5), ERROR(4), WARNING(3), INFO(2), FINE(1);
		
		private final int severity;
		
		private Level(int severity) {
			this.severity = severity;
		}
		
		public boolean includes(Level level) {
			if (level == null) {
				return false;
			}
			return severity >= level.severity;
		}
		
	}
	
	/**
	 * Logs out the given {@code message} using the given
	 * {@link Level} to {@link System#out}.<br />
	 * <br />
	 * If the given {@code message} is {@code null} no log output is getting produced<br />
	 * If the given {@link Level} is {@code null} the Log {@link Level} {@link Level#INFO} is being assumed.
	 * if the given {@link Level}s severity is not at least as high as the severity of {@link #LEVEL} no log output is getting produced.
	 * 
	 * @param message the message to log out
	 * @param level the Log {@link Level} to log out with.
	 */
	private static void log(String message, Level level) {
		if (message == null) {
			return;
		}
		if (level == null) {
			level = Level.INFO;
		}
		if (true || level.includes(LEVEL)) {
			System.out.println("[" + Thread.currentThread().getName() + "] [DYNATRACE-COMERICA-AGENT] [" + level.name() + "] " + message);
		}
	}

	/**
	 * Logs out the given {@code message} to {@link System#out} with {@link Level#INFO}
	 * unless {@link #LEVEL} prevents that from happening.<br />
	 * <br />
	 * If the given {@code message} is {@code null} no log output is getting produced.
	 * 
	 * @param message the message to log out
	 */
	public static void info(String message) {
		log(message, Level.INFO);
	}
	
	/**
	 * Logs out the given {@code message} to {@link System#out} with {@link Level#FINE}
	 * unless {@link #LEVEL} prevents that from happening.<br />
	 * <br />
	 * If the given {@code message} is {@code null} no log output is getting produced.
	 * 
	 * @param message the message to log out
	 */
	public static void fine(String message) {
		log(message, Level.FINE);
	}
	
	/**
	 * Logs out the given {@code message} to {@link System#out} with {@link Level#WARNING}
	 * unless {@link #LEVEL} prevents that from happening.<br />
	 * <br />
	 * If the given {@code message} is {@code null} no log output is getting produced.
	 * 
	 * @param message the message to log out
	 */
	public static void warn(String message) {
		log(message, Level.WARNING);
	}
	
	/**
	 * Logs out the given {@code message} to {@link System#out} with {@link Level#ERROR}
	 * unless {@link #LEVEL} prevents that from happening.<br />
	 * <br />
	 * If the given {@code message} is {@code null} no log output is getting produced.
	 * 
	 * @param message the message to log out
	 */
	public static void error(String message, Throwable t) {
		log(message, Level.INFO);
		log(throwableToString(t), Level.INFO);
	}
	
	/**
	 * Helper method for getting a string representation of the given
	 * {@link Throwable}s stacktrace.
	 * 
	 * @param t the {@link Throwable} to produce a string representation for
	 * 
	 * @return the Stack Trace of the given {@link Throwable} as String.
	 */
	private static final String throwableToString(Throwable t) {
		if (t == null) {
			return null;
		}
		try (
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
		) {
			t.printStackTrace(pw);
			pw.flush();
			return sw.getBuffer().toString();
		} catch (Throwable t2) {
			t.printStackTrace(System.err);
			t2.printStackTrace(System.err);
			return null;
		}
	}
}
