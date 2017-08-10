package com.vatsul.awatcher.anidbapi;

import com.vatsul.awatcher.Utils;

public class Amask {	
	
	// All amask fields not implemented, refer to anidb udp API
	private boolean totalEpisodes;
	private boolean highestEpNum;
	private boolean year;
	private boolean type;
	private boolean romajiName;
	private boolean kanjiName;
	private boolean englishName;
	private boolean otherName;
	private boolean shortName;
	private boolean synonymList;
	private boolean epNo;
	private boolean epName;
	private boolean epRomaji;
	private boolean epKanji;
	private boolean epRating;
	private boolean epVoteCount;
	private boolean groupName;
	private boolean groupShortName;

	public Amask() {
		
	}
	
	public String getHexStr() {
		String binaryStr = "00000000000000000000000000000000";
		if(totalEpisodes)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 1, "1");
		if(highestEpNum)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 2, "1");
		if(year)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 3, "1");
		if(type)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 4, "1");
		if(romajiName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+1, "1");
		if(kanjiName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+2, "1");
		if(englishName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+3, "1");
		if(otherName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+4, "1");
		if(shortName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+5, "1");
		if(synonymList)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 8+6, "1");
		if(epNo)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+1, "1");
		if(epName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+2, "1");
		if(epRomaji)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+3, "1");
		if(epKanji)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+4, "1");
		if(epRating)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+5, "1");
		if(epVoteCount)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 16+6, "1");
		if(groupName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+1, "1");
		if(groupShortName)
			binaryStr = Utils.replaceCharacterAt(binaryStr, 24+2, "1");
		
		return Utils.binaryToHexadecimal(binaryStr);
	}
	
	public void setTotalEpisodes(boolean totalEpisodes) {
		this.totalEpisodes = totalEpisodes;
	}

	public void setHighestEpNum(boolean highestEpNum) {
		this.highestEpNum = highestEpNum;
	}

	public void setYear(boolean year) {
		this.year = year;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public void setRomajiName(boolean romajiName) {
		this.romajiName = romajiName;
	}

	public void setKanjiName(boolean kanjiName) {
		this.kanjiName = kanjiName;
	}

	public void setEnglishName(boolean englishName) {
		this.englishName = englishName;
	}

	public void setOtherName(boolean otherName) {
		this.otherName = otherName;
	}

	public void setShortName(boolean shortName) {
		this.shortName = shortName;
	}

	public void setSynonymList(boolean synonymList) {
		this.synonymList = synonymList;
	}

	public void setEpNo(boolean epNo) {
		this.epNo = epNo;
	}

	public void setEpName(boolean epName) {
		this.epName = epName;
	}

	public void setEpRomaji(boolean epRomaji) {
		this.epRomaji = epRomaji;
	}

	public void setEpKanji(boolean epKanji) {
		this.epKanji = epKanji;
	}

	public void setEpRating(boolean epRating) {
		this.epRating = epRating;
	}

	public void setEpVoteCount(boolean epVoteCount) {
		this.epVoteCount = epVoteCount;
	}
	
	public void setGroupName(boolean groupName) {
		this.groupName = groupName;
	}
	
	public void setGroupShortName(boolean groupShortName) {
		this.groupShortName = groupShortName;
	}
}
