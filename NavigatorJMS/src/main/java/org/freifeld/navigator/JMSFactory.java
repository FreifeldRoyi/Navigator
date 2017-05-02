package org.freifeld.navigator;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author royif
 * @since 24/02/17
 */
public class JMSFactory extends PubSubFactory
{
	private InitialContext context;
	private ConnectionFactory factory;

	public JMSFactory(SerializationFactory serializationFactory, Class<?>... supportedTypes)
	{
		super(serializationFactory, supportedTypes);
		try
		{
			this.context = new InitialContext();
			this.factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		}
		catch (NamingException e)
		{
			//TODO logs
			e.printStackTrace();
		}
	}

	@Override
	protected <T> Publisher<T> createLegalPublisher(Class<T> cls, String topic)
	{
		Publisher<T> toReturn = null;
		try
		{
			Topic lookupTopic = (Topic) context.lookup("topic/" + topic);
			toReturn = new JMSPublisher<>(cls, this.serializationFactory.createSerializer(cls), lookupTopic, this.factory.createConnection());
		}
		catch (NamingException | JMSException e)
		{
			//TODO logs
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	protected <T> Subscriber<T> createLegalSubscriber(Class<T> cls, String topic)
	{
		Subscriber<T> toReturn = null;

		try
		{
			Topic lookupTopic = (Topic) context.lookup("topic/" + topic);
			toReturn = new JMSSubscriber<>(cls, this.serializationFactory.createDeserializer(cls), lookupTopic, this.factory.createConnection(), null);
		}
		catch (NamingException | JMSException e)
		{
			//TODO logs
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public void close()
	{
		try
		{
			this.context.close();
		}
		catch (NamingException e)
		{
			//TODO
			e.printStackTrace();
		}
	}
}
