/*
 * Created on Apr 26, 2003
 * Version $Id$
 */
package com.pnehrer.logging;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class SystemLogHandler extends Handler {
	
	private static final Map levelMap = new HashMap(7);
	
	static {
		System.loadLibrary("syslog");
		
		levelMap.put(Level.SEVERE, new Integer(7));
		levelMap.put(Level.WARNING, new Integer(6));
		levelMap.put(Level.INFO, new Integer(5));
		levelMap.put(Level.CONFIG, new Integer(4));
		levelMap.put(Level.FINE, new Integer(3));
		levelMap.put(Level.FINER, new Integer(2));
		levelMap.put(Level.FINEST, new Integer(1));
	}
	
	private final int handle;
	private final Formatter formatter = new Formatter() {
		public String format(LogRecord record) {
			return MessageFormat.format(MessageFormat.format("{0} {1} ",
				new Object[] {record.getLoggerName(), record.getLevel()})
					+ record.getMessage(), record.getParameters());
		}
	};

	/**
	 * 
	 */
	public SystemLogHandler() {
		handle = openLog(Thread.currentThread().getName());
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord record) {
		Integer level = (Integer)levelMap.get(record.getLevel());
		writeLog(handle, level == null ? 0 : level.intValue(),
			formatter.format(record));
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	public void flush() {
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	public void close() throws SecurityException {
		closeLog(handle);
	}

	private static native int openLog(String identifier);
	
	private static native void closeLog(int handle);
	
	private static native void writeLog(int handle, int level, String message);
}
