package com.appspot.ginkotag.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.filters.StringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class RealRabbitCommand implements IRabbitCommand {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RealRabbitCommand.class.getName());

	private static final String MESSAGE_NODE = "message";

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
			LOGGER.error("Error occurred when executing rabbit command", e);
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
		Document document = constructeur.parse(new StringInputStream(
				responseContent));

		Element messageElement = document.getElementById(MESSAGE_NODE);
		return RabbitCommandResponse.valueOf(messageElement.getNodeValue());
	}

	private InputStream sendCommand() throws MalformedURLException, IOException {
		String violetApiUrl = VioletAPI.URL + "?" + VioletAPI.SN_PARAM + "="
				+ rabbit.getSn() + "&" + VioletAPI.TOKEN_PARAM + "="
				+ rabbit.getToken() + "&" + VioletAPI.TTS_PARAM + "="
				+ textToSay;
		URL nabaztagURL = new URL(violetApiUrl);
		URLConnection nabaztagConnection = nabaztagURL.openConnection();
		nabaztagConnection.setReadTimeout(10);
		return nabaztagConnection.getInputStream();
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
