package com.vatsul.awatcher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Utils {
	
	// Checks directory recursively for video files and returns them in an ArrayList
	public static ArrayList<File> getVideoFiles(File directory) {
		String[] videoFormats = {".avi", ".mkv", ".mp4", ".ogm", ".wmv", ".mpg", ".mpeg"};
		ArrayList<File> videoFiles = new ArrayList<File>();
		for(File f : getFilesRecursively(directory)) {
			for(String s : videoFormats) {
				if(f.getName().endsWith(s))
					videoFiles.add(f);
			}
		}
		return videoFiles;
	}
	
	public static String encodeAbsoluteFilepath(File file) {
		try {
			return URLEncoder.encode(file.getAbsolutePath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static File decodeAbsoluteFilepath(String encodedFilepath) {
		try {
			return new File(URLDecoder.decode(encodedFilepath, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<File> getFilesRecursively(File directory) {
		File[] contents = directory.listFiles();
		ArrayList<File> files = new ArrayList<File>(); // To return
		for(File file : contents) {
			if(file.isFile())
				files.add(file);
			else if(file.isDirectory())
				files.addAll(getFilesRecursively(file));
		}
		return files;
	}
	
	// Replaces character at a given location with a given value
	public static String replaceCharacterAt(String input, int position, String value) {
		return input.substring(0, position-1)+value+input.substring(position);
	}
	
	// Converts binary string to hexadecimal string
	public static String binaryToHexadecimal(String binary) {
		String hexadecimal = "";
		for(int i=0; i<binary.length()/4; i++) {
			hexadecimal = hexadecimal+Integer.toHexString(Integer.parseInt(binary.substring(i*4, i*4+4), 2));
		}
		return hexadecimal;
	}
}
