package com.appspot.ginkotag.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeInfo {
	private String lineNumber;
	private String lineName;
	private List<String> times;

	public TimeInfo(String lineNumber, String lineName) {
		this.lineNumber = lineNumber;
		this.lineName = lineName;
		times = new ArrayList<String>();
	}

	public void addTime(String strippedLine) {
		times.add(strippedLine);
	}

	public String getLineName() {
		return lineName;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public List<String> getTimes() {
		return Collections.unmodifiableList(times);
	}

	@Override
	public String toString() {
		return "TimeInfo{" + "lineNumber='" + lineNumber + '\''
				+ ", lineName='" + lineName + '\'' + ", times=" + times + '}';
	}

}
