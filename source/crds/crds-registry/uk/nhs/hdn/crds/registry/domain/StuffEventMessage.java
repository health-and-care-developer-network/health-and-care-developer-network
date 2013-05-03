package uk.nhs.hdn.crds.registry.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.number.NhsNumber;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class StuffEventMessage
{
	@NotNull private final NhsNumber patientIdentifier;
	@NotNull private final ProviderIdentifier providerIdentifier;
	@NotNull private final RepositoryIdentifier repositoryIdentifier;
	@NotNull private final StuffIdentifier stuffIdentifier;
	@NotNull private final StuffEvent stuffEvent;

	public StuffEventMessage(@NotNull final NhsNumber patientIdentifier, @NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final StuffIdentifier stuffIdentifier, @NotNull final StuffEvent stuffEvent)
	{
		this.patientIdentifier = patientIdentifier;
		this.providerIdentifier = providerIdentifier;
		this.repositoryIdentifier = repositoryIdentifier;
		this.stuffIdentifier = stuffIdentifier;
		this.stuffEvent = stuffEvent;
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	@NonNls
	public String toWireFormat()
	{
		return format(ENGLISH, "\"%1$s\",\"%2$s\",\"%3$s\",\"%4$s\",%5$s", patientIdentifier.normalised(), providerIdentifier.toUuidString(), repositoryIdentifier.toUuidString(), stuffIdentifier.toUuidString(), stuffEvent.toWriteFormat());
	}

	@NotNull
	public NhsNumber patientIdentifier()
	{
		return patientIdentifier;
	}

	@NotNull
	public ProviderIdentifier providerIdentifier()
	{
		return providerIdentifier;
	}

	@NotNull
	public RepositoryIdentifier repositoryIdentifier()
	{
		return repositoryIdentifier;
	}

	@NotNull
	public StuffIdentifier stuffIdentifier()
	{
		return stuffIdentifier;
	}

	@NotNull
	public StuffEvent stuffEvent()
	{
		return stuffEvent;
	}

	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s)", getClass().getSimpleName(), toWireFormat());
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final StuffEventMessage that = (StuffEventMessage) obj;

		if (!patientIdentifier.equals(that.patientIdentifier))
		{
			return false;
		}
		if (!providerIdentifier.equals(that.providerIdentifier))
		{
			return false;
		}
		if (!repositoryIdentifier.equals(that.repositoryIdentifier))
		{
			return false;
		}
		if (!stuffIdentifier.equals(that.stuffIdentifier))
		{
			return false;
		}
		if (!stuffEvent.equals(that.stuffEvent))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public int hashCode()
	{
		int result = providerIdentifier.hashCode();
		result = 31 * result + repositoryIdentifier.hashCode();
		result = 31 * result + stuffIdentifier.hashCode();
		result = 31 * result + patientIdentifier.hashCode();
		result = 31 * result + stuffEvent.hashCode();
		return result;
	}
}
