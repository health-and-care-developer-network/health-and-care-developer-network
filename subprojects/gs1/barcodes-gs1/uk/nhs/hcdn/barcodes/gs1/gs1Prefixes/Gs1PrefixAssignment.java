package uk.nhs.hcdn.barcodes.gs1.gs1Prefixes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.common.naming.ActualName;

public interface Gs1PrefixAssignment extends ActualName
{
	boolean isISMN(@NotNull final Digit third, @NotNull final Digit fourth);
}
