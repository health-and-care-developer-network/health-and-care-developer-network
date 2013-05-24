package uk.nhs.hdn.crds.registry.server;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;

import java.util.concurrent.BlockingQueue;

public interface HazelcastConfiguration
{
	//int DefaultHazelcastPort = 5701;
	int DefaultHazelcastPort = 7850;
	@NotNull String QueueName = "stuff-event";

	@NotNull
	BlockingQueue<StuffEventMessage> rootQueue();
}
