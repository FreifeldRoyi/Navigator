package org.freifeld.navigator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author royif
 * @since 07/04/17
 */
public class KafkaPulisher<K, T> extends Publisher<T>
{
	protected KafkaProducer<K, T> kafkaProducer;

	public KafkaPulisher(Properties properties, String topic, Serializer<K> keySerializer, Serializer<T> valueSerializer)
	{
		super(valueSerializer.getType(), valueSerializer, topic);
		this.kafkaProducer = new KafkaProducer<>(properties, new KafkaNavigatorSerializer<>(keySerializer), new KafkaNavigatorSerializer<>(valueSerializer));
	}

	@Override
	public void close()
	{
		super.close();
		this.kafkaProducer.close();
	}

	@Override
	public void fire(T data, SerializationType serializationType)
	{
		fireAsync(data, serializationType); //Note that kafka always sends records asynchronously so... It doesn't matter
	}

	@Override
	public CompletableFuture<Object> fireAsync(T data, SerializationType serializationType)
	{
		CompletableFuture<Object> toReturn = new CompletableFuture<>();
		ProducerRecord<K, T> record = new ProducerRecord<>(this.topic, data);
		Future<RecordMetadata> sentRecord = this.kafkaProducer.send(record, (metadata, exception) ->
		{
			//TODO add logs or some other handling? Maybe incorporate some callback to the navigator framework
			if (exception != null)
			{
				toReturn.completeExceptionally(exception);
			}
			else
			{
				toReturn.complete(metadata);
			}
		});

		return toReturn;
	}
}
