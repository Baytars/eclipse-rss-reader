package com.pnehrer.tools.morphine;

import java.io.File;

import org.xml.sax.InputSource;

/**
 * @author Peter Nehrer
 */
public class FileInputSource extends InputSource {

	private File file;

	public FileInputSource(String path) {
		file = new File(path);
	}
	
	public FileInputSource(File file) {
		this.file = file;
	}
	
	public FileInputSource() {
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
}
