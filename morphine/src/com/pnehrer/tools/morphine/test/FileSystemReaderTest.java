package com.pnehrer.tools.morphine.test;

import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import junit.framework.TestCase;

import org.apache.xalan.serialize.Method;
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xml.serialize.OutputFormat;

import org.xml.sax.SAXException;

import com.pnehrer.tools.morphine.FileSystemReader;

/**
 * @author Peter Nehrer
 */
public class FileSystemReaderTest extends TestCase {

	/**
	 * Constructor for FileSystemReaderTest.
	 * @param arg0
	 */
	public FileSystemReaderTest(String arg0) {
		super(arg0);
	}

	/*
	 * Test for void parse(String)
	 */
	public void testParseString() throws IOException, SAXException {
		Properties props = OutputProperties.getDefaultMethodProperties(Method.XML);
		props.setProperty(OutputKeys.INDENT, "yes");
		props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		props.setProperty(OutputKeys.STANDALONE, "yes");
		
		Serializer serializer = SerializerFactory.getSerializer(props);
		serializer.setOutputStream(System.out);
		
		FileSystemReader fsReader = new FileSystemReader();
		fsReader.setContentHandler(serializer.asContentHandler());
		fsReader.parse(".");
	}

}
