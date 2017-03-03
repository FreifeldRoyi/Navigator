package org.freifeld.navigator;

import javax.jms.*;

/**
 * @author royif
 * @since 03/03/17
 */
public class JMSSubscriber<T> extends Subscriber<T>
{
	private final Connection connection;
	private final Topic topic;
	private final Session session;
	private final MessageConsumer consumer;

	public JMSSubscriber(Class<T> type, Deserializer<T> deserializer, Topic topic, Connection connection) throws JMSException
	{
		super(type, deserializer, topic.getTopicName());
		this.connection = connection;
		this.topic = topic;
		this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.consumer = this.session.createConsumer(topic);
		this.connection.start();
	}

	@Override
	public T feed()
	{
		T toReturn = null;
		Message message = null;
		try
		{
			message = this.consumer.receive();
			toReturn = this.assembleMessage(message);

		}
		catch (JMSException e)
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
			this.connection.stop();
			this.connection.close();
		}
		catch (JMSException e)
		{
			//TODO logs
			e.printStackTrace();
		}
	}

	private T assembleMessage(Message message) throws JMSException
	{
		T toReturn = null;
		String jmsType = message.getJMSType();
		SerializationType type = SerializationType.valueOf(jmsType);
		switch (type)
		{
			case BINARY:
			{
				BytesMessage bytesMessage = (BytesMessage) message;
				int length = bytesMessage.getIntProperty("length");
				byte[] bytes = new byte[length];
				bytesMessage.readBytes(bytes, length);
				toReturn = this.deserializer.deserialize(bytes);
				break;
			}
			case STRING:
			{
				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();
				toReturn = this.deserializer.deserialize(text);
				break;
			}
		}

		return toReturn;
	}
}
