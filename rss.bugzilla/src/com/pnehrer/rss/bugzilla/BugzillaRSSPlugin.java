/*
 * Created on Mar 11, 2005
 * Version $Id$
 */
package com.pnehrer.rss.bugzilla;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/**
 * @author pnehrer
 * 
 */
public class BugzillaRSSPlugin extends Plugin {
	
	private static BugzillaRSSPlugin instance;
	
	public BugzillaRSSPlugin() {
		instance = this;
	}
	
	public static BugzillaRSSPlugin getDefault() {
		return instance;
	}

	public void log(Object entry) {
		IStatus status;
		if (entry instanceof IStatus)
			status = (IStatus) entry;
		else if (entry instanceof Throwable)
			status = new Status(Status.ERROR, getBundle().getSymbolicName(), 0,
					"Unexpected error occurred.", (Throwable) entry);
		else
			status = new Status(Status.INFO, getBundle().getSymbolicName(), 0,
					String.valueOf(entry), null);

		getLog().log(status);
	}
}
