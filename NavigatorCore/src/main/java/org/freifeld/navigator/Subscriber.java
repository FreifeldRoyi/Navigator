package org.freifeld.navigator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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

	/**
	 * A synchronous (blocking) call to receive a new message
	 * @return object T
	 */
	public abstract T feed();

	/**
	 * Async wrapper for {@link #feed} runs on the default Java ExecutorService
	 * @return {@link CompletableFuture} of type T
	 */
	public CompletableFuture<T> feedAsync()
	{
		return CompletableFuture.supplyAsync(this::feed);
	}

	/**
	 * Async wrapper for {@link #feed} runs on the default Java ExecutorService
	 * @param service - The {@link ExecutorService} to run on
	 * @return {@link CompletableFuture} of type T
	 */
	public CompletableFuture<T> feedAsync(ExecutorService service)
	{
		return CompletableFuture.supplyAsync(this::feed, service);
	}

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
