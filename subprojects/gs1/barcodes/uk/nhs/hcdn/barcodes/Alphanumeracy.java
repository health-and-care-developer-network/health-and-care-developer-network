package uk.nhs.hcdn.barcodes;

import org.jetbrains.annotations.NotNull;

public enum Alphanumeracy
{
	NoneAlphanumeric(0),
	FortyOneAlphanumeric(41),
	FortyEightAlphanumeric(48),
	Gs1DataMatrixAlphanumeric(2335),
	;

	private final int alphanumeracy;

	Alphanumeracy(final int alphanumeracy)
	{
		this.alphanumeracy = alphanumeracy;
	}

	public int numeracy()
	{
		return alphanumeracy;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return Integer.toString(alphanumeracy);
	}
}
