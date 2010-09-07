package com.appspot.ginkotag;

import com.sun.jndi.dns.DnsName;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: youri
 * Date: Sep 5, 2010
 * Time: 4:24:51 PM
 * To change this template use File | Settings | File Templates.
 */
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

    @Override
    public String toString() {
        return "TimeInfo{" +
                "lineNumber='" + lineNumber + '\'' +
                ", lineName='" + lineName + '\'' +
                ", times=" + times +
                '}';
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
     public static void main(String[] args) throws UnsupportedEncodingException, MalformedURLException {
        System.out.println(" = " + new URL("http://"+URLEncoder.encode("prés", "UTF-8")));
        System.out.println(" = " + new URL("http://prés"));
    }
}
