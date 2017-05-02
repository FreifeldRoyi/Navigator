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

	public Object serialize(T data, SerializationType type)
	{
		Object toReturn = null;
		switch (type)
		{
			case BINARY:
			{
				toReturn = serializeToBytes(data);
				break;
			}
			case STRING:
			{
				toReturn = serializeToString(data);
				break;
			}
		}

		return toReturn;
	}

	@Override
	public void close()
	{
		//Left blank intentionally
	}

	public Class<T> getType()
	{
		return this.type;
	}
}
