/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.tuples.Pair;

public enum AdditionalInformationKey
{
	PostCode,
	;

	@NotNull
	public Pair<AdditionalInformationKey, Object> with(@NonNls @NotNull final Object value)
	{
		return new Pair<>(this, value);
	}
}
