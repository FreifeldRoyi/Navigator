package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class Subscriber<T> implements AutoCloseable
{
	protected final Deserializer<T> deserializer;
	protected final Class<T> type;
	protected final String topic;

	public Subscriber(Class<T> type, Deserializer<T> deserializer, String topic)
	{
		this.type = type;
		this.deserializer = deserializer;
		this.topic = topic;
	}

	public abstract T feed();

	public Class<T> getType()
	{
		return this.type;
	}

	@Override
	public void close()
	{
		// left blank intentionally
	}
}
