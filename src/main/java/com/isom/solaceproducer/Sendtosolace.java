package com.isom.solaceproducer;

import javax.xml.transform.*;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.XMLMessageProducer;

@Component
public class Sendtosolace {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sendtosolace.class);
	
	public void publishToSolaceTopic(MessageMetadata msgdetails,JSONObject payload)
			throws JCSMPException, TransformerException {
		
		String enterprisecode = payload.getJSONObject("Shipment").getString("EnterpriseCode");
		String mergenode = payload.getJSONObject("Shipment").getString("MergeNode");
		String shipnode = payload.getJSONObject("Shipment").getString("ShipNode");
		
		// Create a JCSMP Session
		final JCSMPProperties properties = new JCSMPProperties();

		properties.setProperty(JCSMPProperties.HOST, msgdetails.getSolacehost()); // host:port
		properties.setProperty(JCSMPProperties.USERNAME, msgdetails.getSolaceuser()); // client-username
		properties.setProperty(JCSMPProperties.PASSWORD, msgdetails.getSolacepassword());

		final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
		session.connect();

		final Topic topic = JCSMPFactory.onlyInstance().createTopic("ingka/isom/Shipment/"+msgdetails.getSrccompartment()+"/"+enterprisecode+"/Return/"+msgdetails.getSrcevent()+"/"+mergenode+"/"+shipnode+"/"+msgdetails.getSrcversion());

		/** Anonymous inner-class for handling publishing events */
		XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
			@Override
			public void responseReceived(String messageID) {

			}

			@Override
			public void handleError(String messageID, JCSMPException e, long timestamp) {
				LOGGER.error("Producer received error for msg:" + messageID + timestamp + e.getStackTrace());

			}
		});

		TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
		msg.setDeliveryMode(DeliveryMode.PERSISTENT);//
		msg.setApplicationMessageId(msgdetails.getSrcmessageid());

		msg.setText(msgdetails.getFinalmessage().toString());

		LOGGER.info("Connected. About to send message to " + topic);
		
		LOGGER.info(msgdetails.getFinalmessage().toString());

		prod.send(msg, topic);

		LOGGER.info("Message sent. Exiting.");

		session.closeSession();

	}

	
	
}
