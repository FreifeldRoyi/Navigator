package org.freifeld.navigator;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author royif
 * @since 24/02/17
 */
public class PubSubFactoryTest
{
	private PubSubFactory factory;

	public PubSubFactoryTest(PubSubFactory factory)
	{
		this.factory = factory;
	}

	@Test
	public void testIsTypeSupported()
	{
		Assert.assertTrue(this.factory.getSupportedTypes().stream().allMatch(cls -> this.factory.isTypeSupported(cls)));
	}

	@Test
	public void testIsTypeNotSupported()
	{
		Assert.assertFalse(this.factory.isTypeSupported(TestType.class));
	}

	@Test
	public void testCreateSubscriber(Class<?> cls, String topic)
	{
		Subscriber<?> subscriber = this.factory.createSubscriber(cls, topic);
		if (this.factory.isTypeSupported(cls))
		{
			Assert.assertNotNull(subscriber);
		}
		else
		{
			Assert.assertNull(subscriber);
		}
	}

	@Test
	public void testCreatePublisher(Class<?> cls, String topic)
	{
		Publisher<?> publisher = this.factory.createPublisher(cls, topic);
		if (this.factory.isTypeSupported(cls))
		{
			Assert.assertNotNull(publisher);
		}
		else
		{
			Assert.assertNull(publisher);
		}
	}

	/**
	 * used for isTypeSupported Test
	 */
	private static class TestType
	{
	}
}