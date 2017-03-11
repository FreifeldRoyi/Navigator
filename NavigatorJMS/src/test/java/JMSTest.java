import org.freifeld.navigator.JMSFactory;
import org.freifeld.navigator.Publisher;
import org.freifeld.navigator.SerializationType;

import java.util.Collections;

/**
 * @author royif
 * @since 03/03/17
 */
public class JMSTest
{
	public static void main(String[] args)
	{
		JMSFactory jmsFactory = new JMSFactory(null, Collections.emptySet());
		Publisher<User> userPublisher = jmsFactory.createPublisher(User.class, "freifeld");
		userPublisher.fire(new User("royif", 28), SerializationType.STRING);
	}
}
