package com.pnehrer.tools.morphine;

import java.io.*;
import org.xml.sax.*;

/**
 * #@# #$#
 */

public class BindletCleaner extends BindletTool {

    public BindletCleaner() {
    }

    protected void onBindlet(String src, String dest) throws SAXException {
        File bindletFile = destDir == null ? new File(dest) : new File(destDir, dest);
        if(bindletFile.exists() && bindletFile.canWrite()) bindletFile.delete();
    }
}
