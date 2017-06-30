package org.freifeld.navigator;

import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;
import java.util.UUID;

/**
 * @author royif
 * @since 01/04/17
 */
public class KafkaTester
{
	public static void main(String[] args) throws InterruptedException
	{
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "tester");
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("group.id", "testers");
		KafkaPulisher<String, String> pub = new KafkaPulisher<>(props, "test", new SimpleSerializer(String.class), new SimpleSerializer(String.class));
		SimpleDeserializer des = new SimpleDeserializer(String.class);
		SimpleSerializer ser = new SimpleSerializer(String.class);
		KafkaStreamSubscriber<String, String> sub = new KafkaStreamSubscriber<>(props, "test", ser, des, ser, des, (s, s2) -> System.out.println("key = " + s + ", value = " + s2));
		//		KafkaSubscriber<String, String> sub = new KafkaSubscriber<>(props, "test", String.class, new SimpleDeserializer(String.class), String.class, new SimpleDeserializer(String.class), null);

		for (int i = 0; i < 100; i++)
		{
			pub.fire("This is a data " + i, SerializationType.STRING);
			//			CompletableFuture<List<String>> future = sub.feedAsync();
			//			future.whenComplete((strings, throwable) -> strings.forEach(System.out::println));
		}
	}

	private static class SimpleSerializer extends Serializer<String>
	{
		public SimpleSerializer(Class<String> type)
		{
			super(type);
		}

		@Override
		public String serializeToString(String data)
		{
			return data;
		}

		@Override
		public byte[] serializeToBytes(String data)
		{
			return data != null ? data.getBytes() : UUID.randomUUID().toString().getBytes();
		}
	}

	private static class SimpleDeserializer extends Deserializer<String>
	{

		public SimpleDeserializer(Class<String> type)
		{
			super(type);
		}

		@Override
		String deserialize(String str)
		{
			return str;
		}

		@Override
		String deserialize(byte[] bytes)
		{
			return new String(bytes);
		}
	}
}
