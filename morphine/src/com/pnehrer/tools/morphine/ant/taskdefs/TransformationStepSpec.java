package com.pnehrer.tools.morphine.ant.taskdefs;

import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.xml.sax.*;

import com.pnehrer.tools.morphine.ant.types.*;

public abstract class TransformationStepSpec extends DataType {

    protected Transform task;
    protected List params;

    public TransformationStepSpec() {
        params = new ArrayList();
    }

    public void setTask(Transform task) {
        this.task = task;
    }

    public Object createParam() {
        NameValuePair param = new NameValuePair();
        params.add(param);
        return param;
    }

    public abstract ContentHandler chain(ContentHandler next) throws BuildException;
}
