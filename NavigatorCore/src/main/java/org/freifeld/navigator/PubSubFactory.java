package org.freifeld.navigator;

import java.util.Collections;
import java.util.Set;

/**
 * @author royif
 * @since 24/02/17
 */
public abstract class PubSubFactory
{
	private final Set<Class<?>> supportedTypes;

	public PubSubFactory(Set<Class<?>> supportedTypes)
	{
		this.supportedTypes = supportedTypes;
	}

	public abstract <T> Publisher<T> createPublisher(Class<T> cls);

	public abstract <T> Subscriber<T> createSubscriber(Class<T> cls);

	public boolean isTypeSupported(Class<?> cls)
	{
		return this.supportedTypes.contains(cls);
	}

	public Set<Class<?>> getSupportedTypes()
	{
		return Collections.unmodifiableSet(supportedTypes);
	}
}
