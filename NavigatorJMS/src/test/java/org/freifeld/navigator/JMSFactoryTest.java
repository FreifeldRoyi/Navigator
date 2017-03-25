package org.freifeld.navigator;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author royif
 * @since 24/03/17
 */
public class JMSFactoryTest
{
	private SerializationFactory serializationFactory;
	private JMSFactory jmsFactory;
	private List<Class<?>> supportedTypes = Arrays.asList(Integer.class, Double.class);
	private EmbeddedJMS embeddedJMS;
	private InitialContext context;

	@BeforeClass
	public void setUp()
	{
		this.initBroker();
		this.serializationFactory = mock(SerializationFactory.class);

		Serializer<Integer> mockIntSerializer = mock(Serializer.class);
		Serializer<Double> mockDoubleSerializer = mock(Serializer.class);

		when(this.serializationFactory.createSerializer(Double.class)).thenReturn(mockDoubleSerializer);
		when(mockDoubleSerializer.serializeToBytes(any())).thenAnswer(invocation -> invocation.getArguments()[0].toString().getBytes());
		when(mockDoubleSerializer.serializeToString(any())).thenAnswer(invocation -> invocation.getArguments()[0].toString());

		when(this.serializationFactory.createSerializer(Integer.class)).thenReturn(mockIntSerializer);
		when(mockIntSerializer.serializeToBytes(any())).thenAnswer(invocation -> invocation.getArguments()[0].toString().getBytes());
		when(mockIntSerializer.serializeToString(any())).thenAnswer(invocation -> invocation.getArguments()[0].toString());

		this.jmsFactory = new JMSFactory(this.serializationFactory, this.supportedTypes.toArray(new Class<?>[] {}));
	}

	@AfterClass
	public void tearDown()
	{
		try
		{
			this.embeddedJMS.stop();
			this.jmsFactory.close();
			this.context.close();
		}
		catch (Exception e)
		{
			Assert.fail("Exception while finalizing JMS-Avro IT", e);
		}
	}

	@Test
	public void typeSupportedTest()
	{
		this.supportedTypes.forEach(cls -> Assert.assertTrue(this.jmsFactory.isTypeSupported(cls)));
	}

	@Test
	public void typeNotSupportedTest()
	{
		Assert.assertFalse(this.jmsFactory.isTypeSupported(NotSupportedClass.class));
		Assert.expectThrows(IllegalArgumentException.class, () -> this.jmsFactory.createPublisher(NotSupportedClass.class, "someTopic"));
		Assert.expectThrows(IllegalArgumentException.class, () -> this.jmsFactory.createSubscriber(NotSupportedClass.class, "someTopic"));
	}

	@Test
	public void createPublisherTest() throws Exception
	{
		try (Publisher<Integer> publisher = this.jmsFactory.createPublisher(Integer.class, "someTopic"))
		{
			Assert.assertNotNull(publisher);
		}
	}

	@Test
	public void createSubscriberTest() throws Exception
	{
		try (Publisher<Integer> publisher = this.jmsFactory.createPublisher(Integer.class, "someTopic"))
		{
			Assert.assertNotNull(publisher);
		}
	}

	private void initBroker()
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
			this.embeddedJMS.start();
		}
		catch (Throwable e)
		{
			Assert.fail("Exception while initializing JMS-Avro IT", e);
		}
	}

	private static class NotSupportedClass
	{
		// Left blank intentionally
	}
}