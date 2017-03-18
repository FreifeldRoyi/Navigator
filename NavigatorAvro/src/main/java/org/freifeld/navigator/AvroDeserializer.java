package org.freifeld.navigator;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author royif
 * @since 17/03/17
 */
public class AvroDeserializer<T> extends Deserializer<T>
{
	private final DecoderFactory factory;
	private final Schema schema;

	public AvroDeserializer(Class<T> type)
	{
		this(type, ReflectData.get().getSchema(type), DecoderFactory.get());
	}

	public AvroDeserializer(Class<T> type, DecoderFactory factory)
	{
		this(type,ReflectData.get().getSchema(type),factory);
	}

	public AvroDeserializer(Class<T> type, Schema schema, DecoderFactory factory)
	{
		super(type);
		this.schema = schema;
		this.factory = factory;
	}

	@Override
	public T deserialize(String str)
	{
		T toReturn = null;
		try
		{
			JsonDecoder decoder = this.factory.jsonDecoder(this.schema, str);
			DatumReader<T> reader = new SpecificDatumReader<>(this.schema);
			toReturn = reader.read(null, decoder);
		}
		catch (IOException e)
		{
			//TODO logs
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public T deserialize(byte[] bytes)
	{
		T toReturn = null;
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes))
		{
			BinaryDecoder decoder = this.factory.binaryDecoder(bytes,null);
			DatumReader<T> reader = new SpecificDatumReader<>(this.schema);
			toReturn = reader.read(null, decoder);
		}
		catch (IOException e)
		{
			//TODO logs
			e.printStackTrace();
		}

		return toReturn;
	}
}
