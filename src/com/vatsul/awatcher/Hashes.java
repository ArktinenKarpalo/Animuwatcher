package com.vatsul.awatcher;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;

public class Hashes {
	
	// Return ed2k hash of the file
	public static String Ed2kHash(File file) {
		try {
			byte[] result;
			byte[] buf;
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			ByteArrayOutputStream hashlist = new ByteArrayOutputStream();
			if(in.available()>9728000) {
				while(in.available()>0) {
					if(in.available()>=9728000)
						buf = new byte[9728000];
					else
						buf = new byte[in.available()];
					in.read(buf);
					hashlist.write(MD4Hash(buf));
				}
				result = MD4Hash(hashlist.toByteArray());
			} else {
				buf = new byte[in.available()];
				in.read(buf);
				result = MD4Hash(buf);
			}
			in.close();
			return gnu.crypto.util.Util.toString(result);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Returns md4 hash of data
	private static byte[] MD4Hash(byte[] input) {
		IMessageDigest md4 = HashFactory.getInstance("MD4");
		md4.update(input, 0, input.length);
		return md4.digest();
	}
}