/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes;

import org.jetbrains.annotations.NotNull;

public interface DigitList extends Comparable<DigitList>
{
	@NotNull
	Digit digitAt(final int index);

	@NotNull
	Digit digitAtPositionT(final int oneBasedPositionT);

	int size();
}
