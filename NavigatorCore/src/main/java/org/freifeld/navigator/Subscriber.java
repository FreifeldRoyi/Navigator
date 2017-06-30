package org.freifeld.navigator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class Subscriber<K, T> implements AutoCloseable
{
	protected final Deserializer<T> deserializer;
	protected final Class<T> type;
	protected final String topic;
	protected final BiConsumer<K, T> consumer;

	public Subscriber(Class<T> type, Deserializer<T> deserializer, String topic, BiConsumer<K, T> consumer)
	{
		this.type = type;
		this.deserializer = deserializer;
		this.topic = topic;
		this.consumer = consumer;
	}

	public boolean isAsyncSubscriber()
	{
		return this.consumer != null;
	}

	/**
	 * A synchronous (blocking) call to receive a new message
	 *
	 * @return object T
	 * @throws IllegalStateException if the Subscriber is in asynchronous mode
	 */
	public List<T> feed()
	{
		if (isAsyncSubscriber())
		{
			throw new IllegalStateException("Cannot use synchronous method while Subscriber is in asynchronous mode");
		}
		return this.legalFeed();
	}

	/**
	 * Async wrapper for {@link #feed} runs on the default Java ExecutorService
	 *
	 * @return {@link CompletableFuture} of type T
	 */
	public CompletableFuture<List<T>> feedAsync()
	{
		return CompletableFuture.supplyAsync(this::feed);
	}

	/**
	 * Async wrapper for {@link #feed} runs on the default Java ExecutorService
	 *
	 * @param service - The {@link ExecutorService} to run on
	 * @return {@link CompletableFuture} of type T
	 */
	public CompletableFuture<List<T>> feedAsync(ExecutorService service)
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

	/**
	 * A synchronous (blocking) call to receive a new message
	 * Assumes that the Subscriber is not in asynchronous mode
	 *
	 * @return object T
	 */
	protected abstract List<T> legalFeed();
}
