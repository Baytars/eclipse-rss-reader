package com.pnehrer.tools.morphine;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.xml.sax.*;

/**
 * #@# #$#
 */

public class BindletGenerator extends BindletTool {

    private Transformer transformer;

    private BindletGenerator() {
    }

    public static BindletGenerator create(Source ao2bindlet)
        throws TransformerConfigurationException {

        return create(ao2bindlet, TransformerFactory.newInstance());
    }

    public static BindletGenerator create(Source ao2bindlet, TransformerFactory factory)
        throws TransformerConfigurationException {

        BindletGenerator instance = new BindletGenerator();
        instance.transformer = factory.newTransformer(ao2bindlet);
        return instance;
    }

    protected void onBindlet(String src, String dest) throws SAXException {
        transformer.clearParameters();
        try {
            transformer.transform(new StreamSource(srcDir == null ? new File(src)
                : new File(srcDir, src)), new StreamResult(destDir == null ?
                    new File(dest) : new File(destDir, dest)));
        }
        catch(TransformerException ex) {
            throw new SAXException(ex);
        }
    }
}
