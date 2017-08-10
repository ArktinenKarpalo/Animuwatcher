package com.vatsul.awatcher.anidbapi;

import com.vatsul.awatcher.Utils;

public class Fmask {

	// All fmask fields not implemented, refer to anidb udp API		
	private boolean aid;
	private boolean eid; 
	private boolean gid;
	private boolean mylistId;
	private boolean otherEpisodes;
	private boolean state;
	private boolean size;
	private boolean ed2k;
	private boolean md5;
	private boolean sha1;
	private boolean crc32;
	private boolean quality;
	private boolean source;
	private boolean audioCodec;
	private boolean audioBitrate;
	private boolean videoCodec;
	private boolean videoBitrate;
	private boolean videoResolution;
	private boolean fileType;
	private boolean dubLanguage;
	private boolean subLanguage;
	private boolean length;
	private boolean description;
	private boolean aired;
	private boolean anidbFilename;
	
	public Fmask() {
		
	}
	
	public String getHexStr() {
		String binaryStr = "00000000000000000000000000000000";
		if(aid)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 2, "1");
		if(eid)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 3, "1");
		if(gid)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 4, "1");
		if(mylistId)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 5, "1");
		if(otherEpisodes)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 6, "1");
		if(state)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8, "1");
		if(size)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+1, "1");
		if(ed2k)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+2, "1");
		if(md5)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+3, "1");
		if(sha1)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+4, "1");
		if(crc32)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+5, "1");
		if(quality)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+1, "1");
		if(source)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+2, "1");
		if(audioCodec)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+3, "1");
		if(audioBitrate)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+4, "1");
		if(videoCodec)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+5, "1");
		if(videoBitrate)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+6, "1");
		if(videoResolution)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+7, "1");
		if(fileType)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+8, "1");
		if(dubLanguage)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+1, "1");
		if(subLanguage)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+2, "1");
		if(length)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+3, "1");
		if(description)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+4, "1");
		if(aired)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+5, "1");
		if(anidbFilename)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+8, "1");
		
		return Utils.binaryToHexadecimal(binaryStr);
	}
	
	public void setAid(boolean aid) {
		this.aid = aid;
	}

	public void setEid(boolean eid) {
		this.eid = eid;
	}

	public void setGid(boolean gid) {
		this.gid = gid;
	}

	public void setMylistId(boolean mylistId) {
		this.mylistId = mylistId;
	}

	public void setOtherEpisodes(boolean otherEpisodes) {
		this.otherEpisodes = otherEpisodes;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void setSize(boolean size) {
		this.size = size;
	}

	public void setEd2k(boolean ed2k) {
		this.ed2k = ed2k;
	}

	public void setMd5(boolean md5) {
		this.md5 = md5;
	}

	public void setSha1(boolean sha1) {
		this.sha1 = sha1;
	}

	public void setCrc32(boolean crc32) {
		this.crc32 = crc32;
	}

	public void setQuality(boolean quality) {
		this.quality = quality;
	}
	public void setSource(boolean source) {
		this.source = source;
	}

	public void setAudioCodec(boolean audioCodec) {
		this.audioCodec = audioCodec;
	}

	public void setAudioBitrate(boolean audioBitrate) {
		this.audioBitrate = audioBitrate;
	}

	public void setVideoCodec(boolean videoCodec) {
		this.videoCodec = videoCodec;
	}

	public void setVideoBitrate(boolean videoBitrate) {
		this.videoBitrate = videoBitrate;
	}

	public void setVideoResolution(boolean videoResolution) {
		this.videoResolution = videoResolution;
	}

	public void setFileType(boolean fileType) {
		this.fileType = fileType;
	}

	public void setDubLanguage(boolean dubLanguage) {
		this.dubLanguage = dubLanguage;
	}

	public void setSubLanguage(boolean subLanguage) {
		this.subLanguage = subLanguage;
	}

	public void setLength(boolean length) {
		this.length = length;
	}

	public void setDescription(boolean description) {
		this.description = description;
	}

	public void setAired(boolean aired) {
		this.aired = aired;
	}

	public void setAnidbFilename(boolean anidbFilename) {
		this.anidbFilename = anidbFilename;
	}
}
