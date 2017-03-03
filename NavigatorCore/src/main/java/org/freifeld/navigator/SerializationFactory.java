package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public interface SerializationFactory
{
	<T> Serializer<T> createSerializer(Class<T> cls);

	<T> Deserializer<T> createDeserializer(Class<T> cls);
}
