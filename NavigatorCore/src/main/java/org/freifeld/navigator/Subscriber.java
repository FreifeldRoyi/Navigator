package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class Subscriber<T> implements AutoCloseable
{
	private Class<T> type;

	public Subscriber(Class<T> type)
	{
		this.type = type;
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
