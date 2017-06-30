package org.freifeld.navigator;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author royif
 * @since 03/03/17
 */
public class JMSSubscriber<T> extends Subscriber<String, T> implements MessageListener
{
	private final Connection connection;
	private final Topic topic;
	private final Session session;
	private final MessageConsumer messageConsumer;

	public JMSSubscriber(Class<T> type, Deserializer<T> deserializer, Topic topic, Connection connection, BiConsumer<String, T> consumer) throws JMSException
	{
		super(type, deserializer, topic.getTopicName(), consumer);
		this.connection = connection;
		this.topic = topic;
		this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.messageConsumer = this.session.createConsumer(topic);
		if (this.consumer != null)
		{
			this.messageConsumer.setMessageListener(this);
		}

		this.connection.start();

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

	@Override
	public void onMessage(Message message)
	{
		try
		{
			List<T> data = this.assembleMessage(message);
			data.forEach(t ->
			{
				try
				{
					this.consumer.accept(message.getJMSMessageID(), t);
				}
				catch (JMSException e)
				{
					//TODO logs
					e.printStackTrace();
				}
			});
		}
		catch (JMSException e)
		{
			//TODO logs
			e.printStackTrace();
		}
	}

	@Override
	protected List<T> legalFeed()
	{
		List<T> toReturn = null;
		Message message;
		try
		{
			message = this.messageConsumer.receive();
			toReturn = this.assembleMessage(message);
		}
		catch (JMSException e)
		{
			//TODO logs
			e.printStackTrace();
		}

		return toReturn;
	}

	private List<T> assembleMessage(Message message) throws JMSException
	{
		List<T> toReturn = new ArrayList<>();
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
				toReturn.add(this.deserializer.deserialize(bytes));
				break;
			}
			case STRING:
			{
				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();
				toReturn.add(this.deserializer.deserialize(text));
				break;
			}
		}

		return toReturn;
	}
}
