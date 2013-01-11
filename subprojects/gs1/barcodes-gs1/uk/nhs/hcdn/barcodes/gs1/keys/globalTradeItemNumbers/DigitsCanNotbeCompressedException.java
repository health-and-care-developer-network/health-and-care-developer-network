package uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.UK;

public final class DigitsCanNotbeCompressedException extends Exception
{
	public DigitsCanNotbeCompressedException(@NotNull @NonNls final String because)
	{
		super(format(UK, "Digits can not be compressed %1$s", because));
	}
}
