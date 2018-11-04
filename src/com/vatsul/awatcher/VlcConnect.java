package com.vatsul.awatcher;

public class VlcConnect extends Thread {
	/*
	 * Enable VLC HTTP interface from Preferences -> All -> Interface -> Main interfaces -> Web
	 * Set password from Preferences -> All -> Interface -> Main Interfaces -> Lua -> Lua HTTP -> Password
	 */
	
	// Returns position of playback on scale 0 to 1
	
	/* This thread is meant to continously check whether VLC http web interface is running or not
	 * check if file is running, if playback has progressed more than 90% check if
	 * malID can be found from database by using the filepath
	 * if it is found then check if the file that is currently being watched is the next episode
	 * of that series that user has not watched yet, if it is then mark it as watched and add the file name to ignore list
	 * Make the % of episode watched when it is marked as watched customizable
	 */
	/*
	public void run() {
		ArrayList<String> markedFilepaths = new ArrayList<String>();
		while(true) {
			if(Main.config.getListenMediaplayers()) {
				Double playbackPosition = getPlaybackPosition(Main.config.getVlcPort(), Main.config.getVlcPassword());
				if(playbackPosition==-1) {
					// VLC Probably not running or http interface could not be found
				} else if(playbackPosition>Main.config.getMarkOnMalPercentage()) {
					File currentFile = VlcConnect.getCurrentTrack(Main.config.getVlcPort(), Main.config.getVlcPassword());
					if(markedFilepaths.contains(currentFile.getAbsolutePath())) {
						// Do nothing
					} else {
						int aid = Main.database.getAid(currentFile);
						int malID = Main.database.getMalID(aid);
						int watchedEpMal = MalApi.getWatchedEpisodes(malID, Main.config.getMalUsername());
						String currentEpNum = Main.database.getEpNum(currentFile);
						if(currentEpNum.matches("^\\d+$")) { // Non-episodes could be labeled as T36 instead of numbers
							if((Integer.parseInt(currentEpNum)-1)==watchedEpMal) {
								MalApi.updateAnimeListWatchedEpisodes(malID, Integer.parseInt(currentEpNum));
								Main.gui.updateDatabaseData();
							}
							markedFilepaths.add(currentFile.getAbsolutePath());
						}
					}
				}
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Double getPlaybackPosition(int port, String password) {
		try {
			URL url = new URL("http://localhost:"+port+"/requests/status.xml");
			String credentials = java.util.Base64.getEncoder().encodeToString((""+":"+password).getBytes());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",  "Basic " + credentials);
			InputStream is = con.getInputStream();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList position = doc.getElementsByTagName("position");
			return Double.parseDouble(position.item(0).getTextContent());
		} catch (IOException | SAXException | ParserConfigurationException | DOMException e) {
			if(e.getClass() == ConnectException.class) {
				return (double) -1; // VLC probably not running
			}
			e.printStackTrace();
		}
		// No valid entry found
		return (double) -1;
	}
	
	// Returns file of current track being played on VLC, webinterface port and password as an input
	public static File getCurrentTrack(int port, String password) {
		try {
			URL url = new URL("http://localhost:"+port+"/requests/playlist.xml");
			String credentials = java.util.Base64.getEncoder().encodeToString((""+":"+password).getBytes());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",  "Basic " + credentials);
			InputStream is = con.getInputStream();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList uris = doc.getElementsByTagName("leaf");
			for(int i=0; i<uris.getLength(); i++) { // oh no
				for(int j=0; j<uris.item(i).getAttributes().getLength(); j++) {
					if(uris.item(i).getAttributes().item(j).getTextContent().equals("current")) {
						for(int k=0; k<uris.item(i).getAttributes().getLength(); k++) {
							if(uris.item(i).getAttributes().item(k).getNodeName().equals("uri")) {
								String absoluteFilepath = uris.item(i).getAttributes().item(k).getTextContent();
								return new File(new URI(absoluteFilepath).getPath());	
							}
						}
					}
				}
			}
		} catch (IOException | SAXException | ParserConfigurationException | DOMException | URISyntaxException e) {
			if(e.getClass() == ConnectException.class) {
				return null; // VLC probably not running
			}
			e.printStackTrace();
		}
		// No valid entry found
		return null;
	}*/
}
