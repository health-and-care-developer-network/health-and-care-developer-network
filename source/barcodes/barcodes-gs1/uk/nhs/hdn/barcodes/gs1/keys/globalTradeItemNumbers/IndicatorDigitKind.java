/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.digits.Digit;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static uk.nhs.hdn.common.digits.Digit.*;
import static uk.nhs.hdn.common.EnumeratedVariableArgumentsHelper.unmodifiableSetOf;

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
