package org.freifeld.navigator;

import java.util.concurrent.CompletableFuture;

/**
 * @author royif
 * @since 11/02/17
 */
public abstract class Publisher<T> implements AutoCloseable
{
	protected final Serializer<T> serializer;
	protected final Class<T> type;
	protected final String topic;

	public Publisher(Class<T> type, Serializer<T> serializer, String topic)
	{
		this.type = type;
		this.serializer = serializer;
		this.topic = topic;
	}

	public abstract void fire(T data, SerializationType serializationType);

	public abstract CompletableFuture<Object> fireAsync(T data, SerializationType serializationType);

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
