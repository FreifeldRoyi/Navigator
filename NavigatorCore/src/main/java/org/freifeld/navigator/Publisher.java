package org.freifeld.navigator;

/**
 * @author royif
 * @since 11/02/17
 */
public abstract class Publisher<T> implements AutoCloseable
{
	private Class<T> type;

	public Publisher(Class<T> type)
	{
		this.type = type;
	}

	public abstract void fire(T data, String topic);

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
