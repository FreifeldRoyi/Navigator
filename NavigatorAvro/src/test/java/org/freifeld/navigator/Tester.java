package org.freifeld.navigator;

import avro.org.freifeld.navigator.User;

/**
 * @author royif
 * @since 03/03/17
 */
public class Tester
{
	public static void main(String[] args)
	{

		AvroSerializer<User> ser = new AvroSerializer<>(User.class);
		User user = new User("user", 30);
		String str = ser.serializeToString(user);
		byte[] bytes = ser.serializeToBytes(user);

		System.out.println(str);
		System.out.println(bytes);
	}
}