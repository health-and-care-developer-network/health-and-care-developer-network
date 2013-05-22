package uk.nhs.hdn.crds.registry.server.application.hazelcast;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HazelcastReceivingMessagesRunnable implements Runnable
{
	@NotNull private final List<StuffEventMessage> drainTo;
	@NotNull private final AtomicBoolean terminationSignal;
	@NotNull private final BlockingQueue<StuffEventMessage> stuffEventMessages;
	@NotNull private final PatientRecordStore patientRecordStore;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public HazelcastReceivingMessagesRunnable(@NotNull final AtomicBoolean terminationSignal, @NotNull final BlockingQueue<StuffEventMessage> stuffEventMessages, @NotNull final PatientRecordStore patientRecordStore)
	{
		this.stuffEventMessages = stuffEventMessages;
		this.patientRecordStore = patientRecordStore;
		drainTo = new ArrayList<>(HazelcastStartReceivingMessagesThread.MaximumDrainSize);
		this.terminationSignal = terminationSignal;
	}

	@Override
	public void run()
	{
		while (shouldContinueExecution())
		{
			final int numberDrained = stuffEventMessages.drainTo(drainTo);
			if (numberDrained == 0)
			{
				continue;
			}
			for(int index = 0; index < numberDrained; index++)
			{
				patientRecordStore.addEvent(drainTo.get(index));
			}
			drainTo.clear();
		}

		// TODO: Shutdown behaviour deliberately undefined - needs to be ordered with respect to other threads
	}

	private boolean shouldContinueExecution()
	{
		return !terminationSignal.get();
	}
}
