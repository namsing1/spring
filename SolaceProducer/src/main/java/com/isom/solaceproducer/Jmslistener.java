package com.isom.solaceproducer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.json.JSONObject;
import org.slf4j.Logger;
import java.io.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class Jmslistener {

	private static final Logger LOGGER = LoggerFactory.getLogger(Jmslistener.class);

	Sendtosolace sender = new Sendtosolace();

	@Value("${jms.errorDestinationName}")
	private String errorDestinationName;
	@Value("${onpremsolace.host}")
	private String solacehost;
	@Value("${onpremsolace.clientusername}")
	private String solaceuser;
	@Value("${onpremsolace.clientpassword}")
	private String solacepassword;

	private Element header;
	
	@Autowired
	private JmsTemplate template;
	private JSONObject finalmessage;

	private MessageMetadata msgdetails = new MessageMetadata();

	@JmsListener(containerFactory = "myFactory", destination = "${jms.destinationName}")
	

	public void receiveMessage(String msj) throws Exception {

		//LOGGER.info("Payload:" + msj);

		try {

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(msj)));
			NodeList agenode = doc.getElementsByTagName("Header");
			header = (Element) agenode.item(0);
			msgdetails.setSrcmessageid(header.getElementsByTagName("MessageID").item(0).getTextContent());
			msgdetails.setSrccompartment(header.getElementsByTagName("Compartment").item(0).getTextContent());
			msgdetails.setSrcevent(header.getElementsByTagName("Event").item(0).getTextContent());
			msgdetails.setSrcversion(header.getElementsByTagName("Version").item(0).getTextContent());

		} catch (Exception E) {
			LOGGER.error("ERROR fetching details from Header");
		}

		// transforming from xml to JSON

		try {
			TransformShipment arraymsg = new TransformShipment();
			finalmessage = arraymsg.transformShipmentToJson(msj);
			msgdetails.setFinalmessage(finalmessage);
			msgdetails.setSolacehost(solacehost);
			msgdetails.setSolaceuser(solaceuser);
			msgdetails.setSolacepassword(solacepassword);
			
		} catch (Exception e) {
			LOGGER.error("ERROR transforming the data to JSON");
		}

		try {
			sender.publishToSolaceTopic(msgdetails,finalmessage);
		} catch (Exception e) {
			LOGGER.error("ERROR sending data to Solace");
			
			pushmessagetoerrorqueue(msj);

			LOGGER.info("Message sent to Error Queue");

		}

	}

	public void pushmessagetoerrorqueue(String message) {
		try {
			template.convertAndSend(errorDestinationName, message);
		} catch (Exception e) {
			LOGGER.error("Could not send message to error queue");
			e.printStackTrace();

		}
	}
}
