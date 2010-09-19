package com.appspot.ginkotag.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.appspot.ginkotag.TextSplitterUtil;

public class RealRabbitCommand implements IRabbitCommand {

	private static final Logger LOGGER = Logger
			.getLogger(RealRabbitCommand.class.getName());

	// in secs
	private static final int RESPONSE_TIMEOUT = 10;
	private static final String MESSAGE_NODE = "message";
	private static final int MAX_TTS_LENGTH = 100;

	private final Rabbit rabbit;
	private String textToSay;

	public RealRabbitCommand(Rabbit rabbit) {
		this.rabbit = rabbit;
		textToSay = "";
	}

	@Override
	public IRabbitCommand say(String text) {
		textToSay += text;
		return this;
	}

	@Override
	public RabbitCommandResponse execute() {
		try {
			LOGGER.info("Sending command to real rabbit " + this);
			InputStream response = sendCommand();
			return readResponse(response);
		} catch (Exception e) {
			LOGGER.warning("Error occurred when executing rabbit command " + e);
		}
		return RabbitCommandResponse.NO_RESPONSE;
	}

	private RabbitCommandResponse readResponse(InputStream response)
			throws IOException, ParserConfigurationException, SAXException {
		BufferedReader br = new BufferedReader(new InputStreamReader(response));

		String line = br.readLine();
		String responseContent = "";
		while (line != null) {
			responseContent += line;
			line = br.readLine();
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructeur;
		constructeur = factory.newDocumentBuilder();
		Document document = constructeur.parse(new InputSource(
				new StringReader(responseContent)));

		Element messageElement = document.getElementById(MESSAGE_NODE);
		return RabbitCommandResponse.valueOf(messageElement.getNodeValue());
	}

	private InputStream sendCommand() throws MalformedURLException, IOException {
		InputStream inputStream = null;
		String[] splittedTextToSay = TextSplitterUtil.split(textToSay,
				MAX_TTS_LENGTH);
		for (String toSay : splittedTextToSay) {
			String violetApiUrl = VioletAPI.URL + "?" + VioletAPI.SN_PARAM
					+ "=" + rabbit.getSn() + "&" + VioletAPI.TOKEN_PARAM + "="
					+ rabbit.getToken() + "&" + VioletAPI.TTS_PARAM + "="
					+ toSay;
			URL nabaztagURL = new URL(violetApiUrl);
			URLConnection nabaztagConnection = nabaztagURL.openConnection();
			nabaztagConnection.setReadTimeout(RESPONSE_TIMEOUT);
			inputStream = nabaztagConnection.getInputStream();
		}
		return inputStream;
	}

	@Override
	public String toString() {
		return "RealRabbitCommand [rabbit=" + rabbit + ", textToSay="
				+ textToSay + "]";
	}

	private static class VioletAPI {
		private static final String URL = "http://api.nabaztag.com/vl/FR/api.jsp";
		private static final String SN_PARAM = "sn";
		private static final String TOKEN_PARAM = "token";
		private static final String TTS_PARAM = "tts";
	}

}
