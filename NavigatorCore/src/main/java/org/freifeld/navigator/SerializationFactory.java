package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public interface SerializationFactory
{
	<T> Serializer<T> createSeriazlier(Class<T> cls);

	<T> Deserializer<T> createDeserializer(Class<T> cls);
}
