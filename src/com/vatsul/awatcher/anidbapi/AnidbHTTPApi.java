package com.vatsul.awatcher.anidbapi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

public class AnidbHTTPApi {

	private static long lastRequestSent;
	
	// Checks cache for annidb info, if not found, downloads it
	public static void cacheAidInfo(int aid, boolean forceUpdate) {
		new File("cache/anidb_anime/").mkdirs();
		File cachedXml = new File("cache/anidb_anime/"+aid+".xml");
		if(forceUpdate || !cachedXml.exists()) {
			// Only one request in every 2 seconds
			if(System.currentTimeMillis()-lastRequestSent<2000) {
				try {
					Thread.sleep(2000-(System.currentTimeMillis()-lastRequestSent));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				URL anidbHttpApi = new URL("http://api.anidb.net:9001/httpapi?request=anime&client=animuwatchhttp&clientver=1&protover=1&aid="+aid);
				GZIPInputStream gis = new GZIPInputStream(anidbHttpApi.openStream());
				Files.copy(gis, Paths.get(cachedXml.toURI()), StandardCopyOption.REPLACE_EXISTING);
				lastRequestSent = System.currentTimeMillis();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
