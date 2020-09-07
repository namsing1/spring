package com.isom.solaceproducer;

import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Component;

import com.solacesystems.jcsmp.JCSMPProperties;

@Configuration
@EnableJms
@Component
public class AppConfig {
	
    // Url to access to the queue o topic
    @Value("${jms.providerUrl}")
    private String providerUrl;
    
    // Number of consumers in the application
    @Value("${jms.connectionType}")
    private String connectionType;
    
    //Solace connectivity details
    @Value("${onpremsolace.host}")
    private String Solacehost;
	@Value("${onpremsolace.clientusername}")
    private String Solaceuser;
	@Value("${onpremsolace.clientpassword}")
    private String Solacepassword;
	
	public String getSolacehost() {
		return Solacehost;
	}

	public String getSolaceuser() {
		return Solaceuser;
	}

	public String getSolacepassword() {
		return Solacepassword;
	}
	   
    // Number of consumers in the application
    @Value("${jms.concurrentConsumers}")
    private String concurrentConsumers;
    
    @Bean(name = "customized")
    public JCSMPProperties createJCSMPProperties() {
    final JCSMPProperties properties = new JCSMPProperties();  
    properties.setProperty(JCSMPProperties.HOST, Solacehost); // host:port
    properties.setProperty(JCSMPProperties.USERNAME, Solaceuser); // client-username
    properties.setProperty(JCSMPProperties.PASSWORD, Solacepassword);
    return properties;
    }
    
    
    @Bean
    public JndiTemplate jndiTemplate(){
        JndiTemplate jndiTemplate =new JndiTemplate();
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial","weblogic.jndi.WLInitialContextFactory");
        properties.setProperty("java.naming.provider.url", providerUrl);
        jndiTemplate.setEnvironment(properties);
        return jndiTemplate;
    }

    @Autowired
    @Bean
    public JndiDestinationResolver jmsDestionationProvider() {
        JndiDestinationResolver destinationResolver = new JndiDestinationResolver();
        destinationResolver.setJndiTemplate(jndiTemplate());
        return destinationResolver;
    }

    @Autowired
    @Bean
    public JndiObjectFactoryBean connectionFactory(){
        JndiObjectFactoryBean cf = new JndiObjectFactoryBean();
        cf.setJndiTemplate(jndiTemplate());
        cf.setJndiName(connectionType);
        return cf;
    }
    
    @Autowired
    @Bean(name="weblogic")
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory((ConnectionFactory) connectionFactory().getObject());
        template.setSessionAcknowledgeModeName("AUTO_ACKNOWLEDGE");
        template.setSessionTransacted(true);
        template.setDestinationResolver(jmsDestionationProvider());
        return template;
    }
    
    @Autowired
    @Bean(name="myFactory")
    public DefaultJmsListenerContainerFactory myFactory(ConnectionFactory connectionFactory, DestinationResolver destination) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(destination);
        factory.setConcurrency(concurrentConsumers);
        return factory;
    }
       
}
