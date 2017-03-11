package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class Serializer<T> implements AutoCloseable
{
	protected final Class<T> type;

	public Serializer(Class<T> type)
	{
		this.type = type;
	}

	public abstract String serializeToString(T data);

	public abstract byte[] serializeToBytes(T data);

	@Override
	public void close() throws Exception
	{
		//Left blank intentionally
	}
}
