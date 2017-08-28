package com.vatsul.awatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Config {

	private File configFile = new File("config");
	private Properties config = new Properties();
	
	public Config() {
		loadConfig();
		initConfig();
	}
	
	private void initConfig() {
		// Set default values for stuff that is missing
		if(config.get("animeDirectory")==null) {
			setValue("animeDirectory", new File(System.getProperty("user.home")).getAbsolutePath()); // A guess
			setValue("vlcPort", "8080");
			setValue("malUsername", "username");
			setValue("malPassword", "password");
			setValue("anidbUsername", "username");
			setValue("anidbPassword", "password");
			setValue("vlcPassword", "password");
			setValue("markOnMalPercentage", "0.90");
			setValue("disableWelcome", "0");
			setValue("listenMediaplayers", "0");
		}
	}
	
	private void loadConfig() {
		try {
			if(!configFile.exists())
				configFile.createNewFile();
			InputStream in = new FileInputStream(configFile);
			config.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setValue(Object key, Object value) {
		config.put(key, value);
		try {
			OutputStream out = new FileOutputStream(configFile);
			config.store(out, "Animuwatcher config");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getValue(Object key) {
		return (String)config.get(key);
	}
	
	public void setListenMediaplayers(boolean listenMediaplayers) {
		if(listenMediaplayers) {
			setValue("listenMediaplayers", "1");
		} else {
			setValue("listenMediaplayers", "0");
		}
	}
	
	public boolean getListenMediaplayers() {
		if(Integer.parseInt(getValue("listenMediaplayers"))==1) {
			return true;
		} else {
			return false;
		}
	}
	
	public File getAnimeDirectory() {
		return new File(getValue("animeDirectory"));
	}
	
	public void setDisableWelcome(boolean disabled) {
		if(disabled) {
			setValue("disableWelcome", ""+1);
		} else {
			setValue("disableWelcome", ""+0);	
		}
	}
	
	public void setAnimeDirectory(File dir) {
		setValue("animeDirectory", dir.getAbsolutePath());
	}
	
	public int getVlcPort() {
		return Integer.parseInt(getValue("vlcPort"));
	}
	
	public void setVlcPort(int port) {
		setValue("vlcPort", ""+port);
	}
	
	public void setMalUsername(String username) {
		setValue("malUsername", username);
	}
	
	public String getMalUsername()  {
		return getValue("malUsername");
	}
	
	public void setMalPassword(String password) {
		setValue("malPassword", password);
	}
	
	public String getMalPassword()  {
		return getValue("malPassword");
	}
	
	public void setAnidbUsername(String username) {
		setValue("anidbUsername", username);
	}
	
	public String getAnidbUsername()  {
		return getValue("anidbUsername");
	}
	
	public void setAnidbPassword(String password) {
		setValue("anidbPassword", password);
	}
	
	public String getAnidbPassword()  {
		return getValue("anidbPassword");
	}
	
	public void setVlcPassword(String password) {
		setValue("vlcPassword", password);
	}
	
	public String getVlcPassword() {
		return getValue("vlcPassword");
	}
	
	public void setMarkOnMalPercentage(String password) {
		setValue("markOnMalPercentage", password);
	}
	
	public long getMarkOnMalPercentage() {
		return Long.parseLong(getValue("markOnMalPercentage"));
	}
	
	public boolean getDisableWelcome() {
		if(Integer.parseInt(getValue("disableWelcome"))==1) {
			return true;
		} else {
			return false;
		}
	}
	
}
