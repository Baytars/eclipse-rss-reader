/*
 * Created on Nov 24, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IRegisteredTranslator extends ITranslator {

    public String getId();

    public String getDescription();
}