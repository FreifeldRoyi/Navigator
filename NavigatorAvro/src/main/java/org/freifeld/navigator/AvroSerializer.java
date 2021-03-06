package org.freifeld.navigator;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author royif
 * @since 03/03/17
 */
public class AvroSerializer<T> extends Serializer<T>
{
	private final EncoderFactory factory;
	private final Schema schema;

	public AvroSerializer(Class<T> type)
	{
		this(type, ReflectData.get().getSchema(type), EncoderFactory.get());
	}

	public AvroSerializer(Class<T> type, EncoderFactory factory)
	{
		this(type, ReflectData.get().getSchema(type), factory);
	}

	public AvroSerializer(Class<T> type, Schema schema, EncoderFactory factory)
	{
		super(type);
		this.schema = schema;
		this.factory = factory;
	}

	@Override
	public String serializeToString(T data)
	{
		String toReturn = null;

		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			JsonEncoder encoder = factory.jsonEncoder(this.schema, out);
			DatumWriter<T> writer = new SpecificDatumWriter<>(this.schema);
			writer.write(data, encoder);
			encoder.flush();
			toReturn = out.toString();
		}
		catch (IOException e)
		{
			//TODO logs
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public byte[] serializeToBytes(T data)
	{
		byte[] toReturn = null;

		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			BinaryEncoder encoder = factory.directBinaryEncoder(out, null);
			DatumWriter<T> writer = new SpecificDatumWriter<>(this.schema);
			writer.write(data, encoder);
			encoder.flush();
			toReturn = out.toByteArray();
		}
		catch (IOException e)
		{
			//TODO logs
			e.printStackTrace();
		}

		return toReturn;
	}
}
