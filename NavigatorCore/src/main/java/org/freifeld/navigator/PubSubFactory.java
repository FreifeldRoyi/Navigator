package org.freifeld.navigator;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class PubSubFactory implements AutoCloseable
{
	protected final Set<Class<?>> supportedTypes;
	protected final SerializationFactory serializationFactory;

	public PubSubFactory(SerializationFactory serializationFactory, Class<?>... supportedTypes)
	{
		this.serializationFactory = serializationFactory;
		this.supportedTypes = Stream.of(supportedTypes).collect(Collectors.toSet());
	}

	public <T> Publisher<T> createPublisher(Class<T> cls, String topic) throws IllegalArgumentException
	{
		if (!supportedTypes.contains(cls))
		{
			throw new IllegalArgumentException("Cannot create Publisher for type " + cls + ". Type not supported");
		}

		return this.createLegalPublisher(cls, topic);
	}

	public <T> Subscriber<T> createSubscriber(Class<T> cls, String topic) throws IllegalArgumentException
	{
		if (!supportedTypes.contains(cls))
		{
			throw new IllegalArgumentException("Cannot create Subscriber for type " + cls + ". Type not supported");
		}

		return this.createLegalSubscriber(cls, topic);
	}

	public boolean isTypeSupported(Class<?> cls)
	{
		return this.supportedTypes.contains(cls);
	}

	public Set<Class<?>> getSupportedTypes()
	{
		return Collections.unmodifiableSet(supportedTypes);
	}

	protected abstract <T> Publisher<T> createLegalPublisher(Class<T> cls, String topic);

	protected abstract <T> Subscriber<T> createLegalSubscriber(Class<T> cls, String topic);
}
