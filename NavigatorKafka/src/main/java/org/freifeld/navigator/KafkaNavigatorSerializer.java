package org.freifeld.navigator;

import java.util.Map;

/**
 * @author royif
 * @since 10/04/17
 */
class KafkaNavigatorSerializer<T> extends Serializer<T> implements org.apache.kafka.common.serialization.Serializer<T>
{
	private Serializer<T> adapted;
	private boolean isKey;
	private Map<String, ?> configs;

	public KafkaNavigatorSerializer(Serializer<T> adapted)
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
	public byte[] serialize(String topic, T data)
	{
		return this.serializeToBytes(data);
	}

	@Override
	public String serializeToString(T data)
	{
		return this.adapted.serializeToString(data);
	}

	@Override
	public byte[] serializeToBytes(T data)
	{
		return this.adapted.serializeToBytes(data);
	}
}
