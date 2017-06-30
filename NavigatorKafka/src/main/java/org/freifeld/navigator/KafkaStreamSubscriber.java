package org.freifeld.navigator;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

/**
 * @author royif
 * @since 29/04/17.
 */
public class KafkaStreamSubscriber<K, T> extends Subscriber<K, T>
{

	private final KStream<K, T> kafkaStream;
	private final StreamsConfig config;
	private final KafkaStreams streams;

	public KafkaStreamSubscriber(Properties props, String topic, Serializer<K> keySer, Deserializer<K> keyDes, Serializer<T> valSer, Deserializer<T> valDes, BiConsumer<K, T> consumer)
	{
		super(valDes.getType(), valDes, topic, consumer);
		KStreamBuilder builder = new KStreamBuilder();
		//		this.kafkaStream = builder.stream(topic);

		this.kafkaStream = builder.stream(Serdes.serdeFrom(new KafkaNavigatorSerializer<>(keySer), new KafkaNavigatorDeserializer<>(keyDes)),
				Serdes.serdeFrom(new KafkaNavigatorSerializer<>(valSer), new KafkaNavigatorDeserializer<>(valDes)), topic);
		this.kafkaStream.foreach(this.consumer::accept);
		this.config = new StreamsConfig(props);
		this.streams = new KafkaStreams(builder, this.config);
		this.streams.start();
	}

	@Override
	public void close()
	{
		super.close();
		this.streams.close();
	}

	@Override
	public List<T> feed()
	{
		throw new UnsupportedOperationException("This operation is not supported for kafka stream");
	}

	@Override
	public CompletableFuture<List<T>> feedAsync()
	{
		throw new UnsupportedOperationException("This operation is not supported for kafka stream");
	}

	@Override
	public CompletableFuture<List<T>> feedAsync(ExecutorService service)
	{
		throw new UnsupportedOperationException("This operation is not supported for kafka stream");
	}

	@Override
	public boolean isAsyncSubscriber()
	{
		return true;
	}

	@Override
	protected List<T> legalFeed()
	{
		return null;
	}
}
