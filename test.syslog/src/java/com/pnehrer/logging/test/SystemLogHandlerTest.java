/*
 * Created on Apr 26, 2003
 * Version $Id$
 */
package com.pnehrer.logging.test;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class SystemLogHandlerTest extends TestCase {

	/**
	 * Constructor for SystemLogHandlerTest.
	 * @param arg0
	 */
	public SystemLogHandlerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SystemLogHandlerTest.class);
	}

	/*
	 * Test for void publish(LogRecord)
	 */
	public void testPublishLogRecord() {
		StringBuffer buf = new StringBuffer(System.getProperty("java.library.path"));
		buf.append(System.getProperty("path.separator"));
		File userDir = new File(System.getProperty("user.dir"));
		File syslogDir = new File(userDir.getParentFile(), "syslog/dist");
		buf.append(syslogDir.getAbsolutePath());
		System.setProperty("java.library.path", buf.toString());
		Logger log = Logger.getLogger("com.pnehrer.logging.test.SystemLogHandlerTest");
		log.log(Level.SEVERE, "testPublishLogRecord");
		log.log(Level.WARNING, "testPublishLogRecord");
		log.log(Level.INFO, "testPublishLogRecord");
		log.log(Level.CONFIG, "testPublishLogRecord");
		log.log(Level.FINE, "testPublishLogRecord");
		log.log(Level.FINER, "testPublishLogRecord");
		log.log(Level.FINEST, "testPublishLogRecord");
	}
}
