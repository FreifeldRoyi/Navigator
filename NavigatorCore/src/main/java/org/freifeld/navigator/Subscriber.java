package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class Subscriber<T> implements AutoCloseable
{
	protected final Deserializer<T> deserializer;
	protected final Class<T> type;

	public Subscriber(Class<T> type, Deserializer<T> deserializer)
	{
		this.type = type;
		this.deserializer = deserializer;
	}

	public abstract T feed(String topic);

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
