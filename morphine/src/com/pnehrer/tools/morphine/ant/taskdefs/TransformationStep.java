package com.pnehrer.tools.morphine.ant.taskdefs;

import org.apache.tools.ant.*;
import org.xml.sax.*;

/**
 * #@# #$#
 */
public interface TransformationStep extends ContentHandler {

    public void setNext(ContentHandler next);
    public void setParameter(String name, Object value);
    public void setProject(Project project);
}
