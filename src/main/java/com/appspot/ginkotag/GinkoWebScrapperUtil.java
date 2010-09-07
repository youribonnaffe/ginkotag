package com.appspot.ginkotag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

public class GinkoWebScrapperUtil {

    public static List<TimeInfo> extractTimes(Reader input) throws IOException {
        BufferedReader fis = new BufferedReader(input);
        String line = fis.readLine();
        while (line != null && !line.contains("<table")) {
            line = fis.readLine();
        }

        List<TimeInfo> timeInfos = new ArrayList<TimeInfo>();
        TimeInfo timeInfo = null;
        while (line != null && !line.contains("</table")) {
            if (line.contains("<td")) {
                String[] stringParts = line.split("</div>");
                String strippedLine = stringParts[0].replaceAll("<.*?>", "").trim();
                String strippedLine2 = null;
                if (stringParts.length > 2) {
                    strippedLine2 = stringParts[1].replaceAll("<.*?>", "").trim();
                    timeInfo = new TimeInfo(strippedLine, strippedLine2);

                    timeInfos.add(timeInfo);
                } else {
                    if (!strippedLine.isEmpty()) {
                        strippedLine.replaceAll("min","minutes");
                        timeInfo.addTime(strippedLine);
                    }
                }
            }
            line = fis.readLine();
        }

        fis.close();
        return timeInfos;
    }


}