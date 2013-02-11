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

package uk.nhs.hdn.barcodes.gs1.organisation.index;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.barcodes.gs1.Gs1CompanyPrefixAndItem;
import uk.nhs.hdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class SubIndex extends AbstractToString implements Gs1CompanyPrefixIndex
{
	// GTIN 8 unsupported. For all others, there are 3 - 8 digits for the GS1 Prefix
	private final Map<Digits, Tuple>[] branchByLengthOfGs1PrefixFromThreeToEight;

	// used by toString
	@SuppressWarnings("FieldCanBeLocal")
	private final int gs1Prefix;

	@SuppressWarnings("unchecked")
	public SubIndex(final int gs1Prefix)
	{
		this.gs1Prefix = gs1Prefix;
		branchByLengthOfGs1PrefixFromThreeToEight = new Map[9];
		for(int index = 3; index < 9; index++)
		{
			branchByLengthOfGs1PrefixFromThreeToEight[index] = new HashMap<>(100);
		}
	}

	public void register(@NotNull final Tuple tuple)
	{
		final Gs1CompanyPrefix gs1CompanyPrefix = tuple.gs1CompanyPrefix();
		final Digits digits = gs1CompanyPrefix.digitsWithoutGs1Prefix();
		if (!digits.hasSizeBetween(3, 8))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Tuple %1$s with duplicate GS-1 company prefix less GS-1 prefix of a size not between 3 and 8", tuple));
		}
		final Map<Digits, Tuple> map = branchByLengthOfGs1PrefixFromThreeToEight[digits.size()];
		final Tuple was = map.put(digits, tuple);
		if (was != null)
		{
			throw new IllegalArgumentException(format(ENGLISH, "%1$s with duplicate GS-1 company prefix of earlier %2$s (later duplicate used)", tuple, was));
		}
	}

	@Override
	@Nullable
	public Tuple find(@NotNull final Gs1CompanyPrefixAndItem gs1CompanyPrefixAndItem)
	{
		Digits digits = gs1CompanyPrefixAndItem.digitsWithoutGs1Prefix(8);
		while(digits.size() >= 3)
		{
			@Nullable final Tuple tuple = branchByLengthOfGs1PrefixFromThreeToEight[digits.size()].get(digits);
			if (tuple != null)
			{
				return tuple;
			}
			digits = digits.slice(1, digits.size());
		}
		return null;
	}
}
