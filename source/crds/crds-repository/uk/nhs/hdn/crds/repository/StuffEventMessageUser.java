package uk.nhs.hdn.crds.repository;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;

public interface StuffEventMessageUser
{
	void use(@NotNull final StuffEventMessage stuffEventMessage);
}
