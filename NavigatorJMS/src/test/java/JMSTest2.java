import org.freifeld.navigator.JMSFactory;
import org.freifeld.navigator.Subscriber;

import java.util.Collections;

/**
 * @author royif
 * @since 03/03/17
 */
public class JMSTest2
{
	public static void main(String[] args)
	{
		JMSFactory jmsFactory = new JMSFactory(null, Collections.emptySet());
		Subscriber<User> userSubscriber = jmsFactory.createSubscriber(User.class, "freifeld");
		userSubscriber.feed();
	}
}
