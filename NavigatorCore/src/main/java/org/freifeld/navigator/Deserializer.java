package org.freifeld.navigator;

/**
 * @author royif
 * @since 24/02/17
 */
public interface Deserializer<T>
{
	T deserialize(String str);

	T deserialize(byte[] bytes);
}
