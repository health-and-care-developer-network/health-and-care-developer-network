package uk.nhs.hdn.crds.repository.senders.hazelcast;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.server.HazelcastConfiguration;

import java.util.concurrent.BlockingQueue;

import static com.hazelcast.client.HazelcastClient.newHazelcastClient;
import static java.net.InetSocketAddress.createUnresolved;

public final class ClientHazelcastConfiguration implements HazelcastConfiguration
{
	@NotNull public static final String DefaultHostname = "services.developer.nhs.uk";

	@NotNull private final HazelcastClient hazelcastClient;

	public ClientHazelcastConfiguration(@NotNull final String hostName, final int portNumber)
	{
		final ClientConfig config = new ClientConfig();
		config.addInetSocketAddress(createUnresolved(hostName, portNumber));
		hazelcastClient = newHazelcastClient(config);
	}

	@NotNull
	@Override
	public BlockingQueue<StuffEventMessage> rootQueue()
	{
		return hazelcastClient.getQueue(QueueName);
	}
}
