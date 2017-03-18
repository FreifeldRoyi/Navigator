package org.freifeld.navigator;

import java.util.Collections;
import java.util.Set;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class PubSubFactory implements AutoCloseable
{
	protected final Set<Class<?>> supportedTypes;
	protected final SerializationFactory serializationFactory;

	public PubSubFactory(SerializationFactory serializationFactory, Set<Class<?>> supportedTypes)
	{
		this.supportedTypes = supportedTypes;
		this.serializationFactory = serializationFactory;
	}

	public abstract <T> Publisher<T> createPublisher(Class<T> cls, String topic);

	public abstract <T> Subscriber<T> createSubscriber(Class<T> cls, String topic);

	public boolean isTypeSupported(Class<?> cls)
	{
		return this.supportedTypes.contains(cls);
	}

	public Set<Class<?>> getSupportedTypes()
	{
		return Collections.unmodifiableSet(supportedTypes);
	}
}
