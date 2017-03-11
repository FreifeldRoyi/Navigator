package org.freifeld.navigator;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.reflect.ReflectData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author royif
 * @since 03/03/17
 */
public class AvroSerializer<T> extends Serializer<T>
{
	private final Schema schema;

	public AvroSerializer(Class<T> type)
	{
		this(type, ReflectData.get().getSchema(type));
	}

	public AvroSerializer(Class<T> type, Schema schema)
	{
		super(type);
		this.schema = schema;
	}

	@Override
	public String serializeToString(T data)
	{
		String toReturn = null;
		EncoderFactory factory = EncoderFactory.get();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			JsonEncoder encoder = factory.jsonEncoder(this.schema, out);
			DatumWriter<T> writer = new GenericDatumWriter<>(this.schema);
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
		EncoderFactory factory = EncoderFactory.get();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			BinaryEncoder encoder = factory.directBinaryEncoder(out, null);
			DatumWriter<T> writer = new GenericDatumWriter<>(this.schema);
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
