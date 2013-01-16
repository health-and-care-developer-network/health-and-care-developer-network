/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.server.Gs1CompanyPrefixResourceStateSnapshot;

public interface Gs1CompanyPrefixResourceStateSnapshotUser
{
	void use(@NotNull final Gs1CompanyPrefixResourceStateSnapshot gs1CompanyPrefixResourceStateSnapshot);
}
