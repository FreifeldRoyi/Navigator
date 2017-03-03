package org.freifeld.navigator;

import javax.jms.*;

/**
 * @author royif
 * @since 25/02/17
 */
public class JMSPublisher<T> extends Publisher<T>
{
	private final Connection connection;
	private final Topic topic;
	private final MessageProducer producer;
	private final Session session;

	public JMSPublisher(Connection connection, Topic topic, Serializer<T> serializer, Class<T> type) throws JMSException
	{
		super(type, serializer);

		this.connection = connection;
		this.topic = topic;
		this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.producer = this.session.createProducer(topic);

	}

	@Override
	public void fire(T data, String topic, SerializationType type)
	{
		try
		{
			Message message = this.createMessage(data, type);
			this.producer.send(this.topic, message);
		}
		catch (JMSException e)
		{
			//TODO logs
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		try
		{
			this.connection.close(); //no need to close sessions and producers once closing the connection
		}
		catch (JMSException e)
		{
			//TODO logs
			e.printStackTrace();
		}
	}

	private Message createMessage(T data, SerializationType type) throws JMSException
	{
		//TODO change to strategy pattern ?
		Message toReturn = null;
		switch (type)
		{
			case BINARY:
			{
				byte[] bytes = this.serializer.serializeToBytes(data);
				BytesMessage message = this.session.createBytesMessage();
				message.writeBytes(bytes);
				message.setIntProperty("length",bytes.length);
				toReturn = message;
				break;
			}
			case STRING:
			{
				String text = this.serializer.serializeToString(data);
				TextMessage message = this.session.createTextMessage();
				message.setText(text);
				toReturn = message;
				break;
			}
		}
		toReturn.setJMSType(type.name());

		return toReturn;
	}
}