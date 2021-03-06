package org.freifeld.navigator;

import avro.org.freifeld.navigator.User;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.server.config.ConnectionFactoryConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.naming.InitialContext;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author royif
 * @since 17/03/17
 */
public class JmsAvroComboIT
{
	private final static String TOPIC_USERS = "Users";
	private InitialContext context;
	private SerializationFactory serializationFactory;
	private PubSubFactory pubSubFactory;
	private EmbeddedJMS embeddedJMS;

	@BeforeClass
	public void setUp()
	{
		Configuration configuration = new ConfigurationImpl().setPersistenceEnabled(false).setJournalDirectory("target/data/journal").setSecurityEnabled(false)
				.addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName()))
				.addConnectorConfiguration("connector", new TransportConfiguration(NettyConnectorFactory.class.getName()));
		JMSConfiguration jmsConfiguration = new JMSConfigurationImpl();
		ConnectionFactoryConfiguration cfConfiguration = new ConnectionFactoryConfigurationImpl().setName("connectionFactory").setConnectorNames(Collections.singletonList("connector"))
				.setBindings("connectionFactory");
		JMSQueueConfiguration queueConfiguration = new JMSQueueConfigurationImpl().setName("Users").setDurable(false).setBindings("topic/Users");

		this.embeddedJMS = new EmbeddedJMS().setConfiguration(configuration).setJmsConfiguration(jmsConfiguration);
		try
		{
			this.context = new InitialContext();
			this.embeddedJMS.setContext(context);

			this.serializationFactory = new AvroSerializationFactory();
			this.pubSubFactory = new JMSFactory(this.serializationFactory, User.class);

			this.embeddedJMS.start();
		}
		catch (Throwable e)
		{
			Assert.fail("Exception while initializing JMS-Avro IT", e);
		}
	}

	@AfterClass
	public void tearDown()
	{
		try
		{
			this.embeddedJMS.stop();
			this.pubSubFactory.close();
			this.context.close();
		}
		catch (Exception e)
		{
			Assert.fail("Exception while finalizing JMS-Avro IT", e);
		}
	}

	@Test
	public void binaryMessageSendTest()
	{
		this.messageSendTest(SerializationType.BINARY);
	}

	@Test
	public void stringMessageSendTest()
	{
		this.messageSendTest(SerializationType.STRING);
	}

	private void messageSendTest(SerializationType type)
	{
		try (Publisher<User> userPublisher = this.pubSubFactory.createPublisher(User.class, TOPIC_USERS);
				Subscriber<String, User> userSubscriber = this.pubSubFactory.createSubscriber(User.class, TOPIC_USERS))
		{
			CompletableFuture<List<User>> future = userSubscriber.feedAsync();
			User jack = new User("Jack", 30);
			userPublisher.fire(jack, type);
			List<User> users = future.join();
			Assert.assertTrue(users.size() == 1);
			Assert.assertEquals(jack, users.get(0));
		}
	}
}
