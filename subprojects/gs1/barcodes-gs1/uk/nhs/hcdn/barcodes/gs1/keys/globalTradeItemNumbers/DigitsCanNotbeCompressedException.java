package uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class DigitsCanNotbeCompressedException extends Exception
{
	public DigitsCanNotbeCompressedException(@NotNull @NonNls final String because)
	{
		super(String.format(Locale.UK, "Digits can not be compressed %1$s", because);
	}
}
