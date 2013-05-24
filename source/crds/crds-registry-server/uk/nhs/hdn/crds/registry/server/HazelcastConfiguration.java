package uk.nhs.hdn.crds.registry.server;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;

import java.util.concurrent.BlockingQueue;

public interface HazelcastConfiguration
{
	int DefaultHazelcastPortNotFirewalled = 7850; // Hazelcast normally runs on 5701 and up
	@NotNull String QueueName = "stuff-event";

	@NotNull
	BlockingQueue<StuffEventMessage> rootQueue();
}
