package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public interface Serializer<T>
{
	String serializeToString(T data);

	byte[] serializeToBytes(T data);
}
