package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public interface Serializer<T>
{
	String serializeToString(T type);

	byte[] serializeToBytes(T type);
}
