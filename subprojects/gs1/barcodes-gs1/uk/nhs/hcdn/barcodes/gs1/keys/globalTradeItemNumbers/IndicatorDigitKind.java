/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static uk.nhs.hcdn.barcodes.Digit.*;
import static uk.nhs.hcdn.common.EnumeratedVariableArgumentsHelper.unmodifiableSetOf;

public enum IndicatorDigitKind
{
	NotPresent(Zero),
	PackagingLevel(One, Two, Three, Four, Five, Six, Seven, Eight),
	VariableMeasureItem(Nine),
	;

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	private static final class CompilerWorkaround
	{
		private static final Map<Digit, IndicatorDigitKind> Index = new EnumMap<Digit, IndicatorDigitKind>(Digit.class);
	}

	private final Set<Digit> validDigitValues;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	IndicatorDigitKind(@NotNull final Digit... validDigitValues)
	{
		this.validDigitValues = unmodifiableSetOf(validDigitValues);
		for (final Digit validDigitValue : validDigitValues)
		{
			if (CompilerWorkaround.Index.put(validDigitValue, this) != null)
			{
				throw new IllegalArgumentException("Duplicate valid digit value");
			}
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static IndicatorDigitKind indicatorDigitKind(@NotNull final Digit digit)
	{
		return CompilerWorkaround.Index.get(digit);
	}

	public boolean isValidFor(@NotNull final Digit digit)
	{
		return validDigitValues.contains(digit);
	}
}
