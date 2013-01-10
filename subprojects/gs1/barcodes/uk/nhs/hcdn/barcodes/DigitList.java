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
