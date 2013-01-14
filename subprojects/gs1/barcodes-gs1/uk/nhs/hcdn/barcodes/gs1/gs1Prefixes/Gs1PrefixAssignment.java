/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.gs1Prefixes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.common.naming.ActualName;

public interface Gs1PrefixAssignment extends ActualName
{
	boolean isISMN(@NotNull final Digit third, @NotNull final Digit fourth);
}
