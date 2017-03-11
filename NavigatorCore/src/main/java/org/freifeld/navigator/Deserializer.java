package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class Deserializer<T> implements AutoCloseable
{
	protected final Class<T> type;

	public Deserializer(Class<T> type)
	{
		this.type = type;
	}

	@Override
	public void close() throws Exception
	{
		//Left blank intentionally
	}

	abstract T deserialize(String str);

	abstract T deserialize(byte[] bytes);
}
