package uk.nhs.hdn.common.serialisers;

import org.jetbrains.annotations.NotNull;

public interface PropertyNameSerialisable extends ValueSerialisable
{
	@NotNull
	String serialiseToPropertyName();
}
