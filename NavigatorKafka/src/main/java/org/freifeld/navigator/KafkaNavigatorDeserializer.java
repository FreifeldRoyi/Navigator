package org.freifeld.navigator;

import java.util.Map;

/**
 * @author royif
 * @since 10/04/17
 */
class KafkaNavigatorDeserializer<T> extends Deserializer<T> implements org.apache.kafka.common.serialization.Deserializer<T>
{
	private Deserializer<T> adapted;
	private boolean isKey;
	private Map<String, ?> configs;

	public KafkaNavigatorDeserializer(Deserializer<T> adapted)
	{
		super(adapted.getType());
		this.adapted = adapted;
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey)
	{
		this.isKey = isKey;
		this.configs = configs;
	}

	@Override
	public T deserialize(String topic, byte[] data)
	{
		return this.deserialize(data);
	}

	@Override
	public T deserialize(String str)
	{
		return this.adapted.deserialize(str);
	}

	@Override
	public T deserialize(byte[] bytes)
	{
		return this.adapted.deserialize(bytes);
	}
}
