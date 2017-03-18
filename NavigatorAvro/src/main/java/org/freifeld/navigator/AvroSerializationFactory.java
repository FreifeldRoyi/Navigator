package org.freifeld.navigator;

import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;

/**
 * @author royif
 * @since 17/03/17
 */
public class AvroSerializationFactory implements SerializationFactory
{
	private final EncoderFactory encoderFactory;
	private final DecoderFactory decoderFactory;

	public AvroSerializationFactory()
	{
		this.encoderFactory = EncoderFactory.get();
		this.decoderFactory = DecoderFactory.get();
	}

	@Override
	public <T> Serializer<T> createSerializer(Class<T> cls)
	{
		return new AvroSerializer<>(cls, this.encoderFactory);
	}

	@Override
	public <T> Deserializer<T> createDeserializer(Class<T> cls)
	{
		return new AvroDeserializer<>(cls, this.decoderFactory);
	}
}
