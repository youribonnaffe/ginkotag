package com.appspot.ginkotag.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.ginkotag.GinkoWebScrapperUtil;
import com.appspot.ginkotag.model.TimeInfo;

public class GinkoTimesWS extends HttpServlet {
	/**	 */
	private static final long serialVersionUID = 1L;

	private static final String WS_PATH = "ginkoTimesWS.do";
	private static final String STOP_PARAM = "stop";
	private static final String TOKEN_PARAM = "token";
	private static final String SN_PARAM = "sn";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String stop = req.getParameter("stop");
		String sn = req.getParameter("sn"); // 002185BA6A34
		String token = req.getParameter("token"); // 1228730228

		stop = URLEncoder.encode(stop, "UTF-8");
		String ginkoURLAsString = "http://www.ginkotempo.com/TempoMobile/tempoMobile.do?choix_station="
				+ stop + "&methode=afficheStation";
		URL ginkoURL = new URL(ginkoURLAsString);
		InputStreamReader urlInput = new InputStreamReader(
				ginkoURL.openStream(), "UTF-8");

		List<TimeInfo> timeInfos = GinkoWebScrapperUtil.extractTimes(urlInput);

		System.out.println(timeInfos);
		String txtMsg = "Arrêt" + blank(3) + stop + blank(5);
		for (TimeInfo timeInfo : timeInfos) {
			if (!timeInfo.getTimes().isEmpty()) {
				txtMsg += blank(5) + "Ligne : " + timeInfo.getLineNumber()
						+ blank(3) + timeInfo.getLineName() + blank(5);
				for (String time : timeInfo.getTimes()) {
					txtMsg += blank(3) + " " + time;
				}
			}
		}
		txtMsg = URLEncoder.encode(txtMsg, "UTF-8");

		String nabaztagURLAsString = "http://api.nabaztag.com/vl/FR/api.jsp?sn="
				+ sn + "&token=" + token + "&tts=" + txtMsg;
		// TODO split message
		URL nabaztagURL = new URL(nabaztagURLAsString);
		URLConnection nabaztagConnection = nabaztagURL.openConnection();
		nabaztagConnection.setReadTimeout(10);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				nabaztagConnection.getInputStream()));

		String line = br.readLine();
		while (line != null) {
			resp.getWriter().println(line);
			line = br.readLine();
		}

		resp.setContentType("text/plain");
		resp.getWriter().println(nabaztagURLAsString);
	}

	private String blank(int i) {
		String ret = "";
		for (int j = 0; j < i; j++) {
			ret += " ";
		}
		return ret;
	}

	public static String getURL(String sn, String token, String stop)
			throws UnsupportedEncodingException {
		return WS_PATH + "?" + SN_PARAM + "=" + sn + "&" + TOKEN_PARAM + "="
				+ token + "&" + STOP_PARAM + "=" + stop;// URLEncoder.encode(stop,
														// "UTF-8");

	}
}
