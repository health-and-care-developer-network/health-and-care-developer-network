package org.gov.data.nhs.hcdn.barcodes;

public enum Digit
{
	Zero(0),
	One(1),
	Two(2),
	Three(3),
	Four(4),
	Five(5),
	Six(6),
	Seven(7),
	Eight(8),
	Nine(9),
	;

	@SuppressWarnings({"ClassWithoutConstructor", "UtilityClassWithoutPrivateConstructor"})
	private static final class CompilerWorkaround
	{
		private static final Digit[] Index = new Digit[10];
	}

	private final int digit;

	Digit(final int digit)
	{
		this.digit = digit;
		CompilerWorkaround.Index[digit] = this;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public int digit()
	{
		return digit;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public static Digit digit(final int digit)
	{
		if (digit < 0 || digit > 9)
		{
			throw new IllegalArgumentException("digit must be between 0 and 9");
		}
		return CompilerWorkaround.Index[digit];
	}
}
