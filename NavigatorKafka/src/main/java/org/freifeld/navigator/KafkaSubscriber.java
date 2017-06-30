package org.freifeld.navigator;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author royif
 * @since 10/04/17
 */
public class KafkaSubscriber<K, T> extends Subscriber<K, T>
{
	protected KafkaConsumer<K, T> kafkaConsumer;
	private ScheduledExecutorService executorService;

	public KafkaSubscriber(Properties properties, String topic, Deserializer<K> keyDeserializer, Deserializer<T> valueDeserializer, BiConsumer<K, T> consumer)
	{
		super(valueDeserializer.getType(), valueDeserializer, topic, consumer);
		this.kafkaConsumer = new KafkaConsumer<>(properties, new KafkaNavigatorDeserializer<>(keyDeserializer), new KafkaNavigatorDeserializer<>(valueDeserializer));
		this.kafkaConsumer.subscribe(Collections.singletonList(topic));
		if (consumer != null)
		{
			this.executorService = Executors.newSingleThreadScheduledExecutor();
			this.executorService.scheduleWithFixedDelay(() -> this.kafkaConsumer.poll(Long.MAX_VALUE).forEach(record -> this.consumer.accept(record.key(), record.value())), 0, 1, TimeUnit.SECONDS);
		}
	}

	@Override
	public void close()
	{
		super.close();
		this.kafkaConsumer.close();
	}

	@Override
	protected List<T> legalFeed()
	{
		List<T> toReturn = new ArrayList<>();
		ConsumerRecords<K, T> poll = this.kafkaConsumer.poll(Long.MAX_VALUE);
		poll.forEach(cons -> toReturn.add(cons.value()));
		return toReturn;
	}
}
