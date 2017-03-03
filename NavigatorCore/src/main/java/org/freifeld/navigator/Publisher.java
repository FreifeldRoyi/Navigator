package org.freifeld.navigator;

/**
 * @author royif
 * @since 11/02/17
 */
public abstract class Publisher<T> implements AutoCloseable
{
	protected final Serializer<T> serializer;
	protected final Class<T> type;

	public Publisher(Class<T> type, Serializer<T> serializer)
	{
		this.type = type;
		this.serializer = serializer;
	}

	public abstract void fire(T data, String topic, SerializationType type);

	public Class<T> getType()
	{
		return this.type;
	}

	@Override
	public void close()
	{
		//Left blank intentionally
	}
}
