package org.freifeld.navigator;

import java.util.Properties;

/**
 * @author royif
 * @since 25/03/17
 */
public class KafkaFactory extends PubSubFactory
{
	private Properties properties;

	public KafkaFactory(Properties properties, SerializationFactory serializationFactory, Class<?>... supportedTypes)
	{
		super(serializationFactory, supportedTypes);

		this.properties = properties;
	}

	@Override
	public void close() throws Exception
	{
		//left blank for now
	}

	@Override
	protected <T> Publisher<T> createLegalPublisher(Class<T> cls, String topic)
	{
		//TODO key serializer sux ???
		// TODO currently I assume that keys are given as an Object
		return null;//new KafkaPulisher<>(topic, this.properties, Object.class, this.serializationFactory.createSerializer(Object.class), cls, this.serializationFactory.createSerializer(cls));
	}

	@Override
	protected <T> Subscriber<T> createLegalSubscriber(Class<T> cls, String topic)
	{
		return null;
	}
}
