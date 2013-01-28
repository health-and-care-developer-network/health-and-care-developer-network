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

package uk.nhs.hdn.common.comparison;

import org.jetbrains.annotations.NotNull;

import static uk.nhs.hdn.common.comparison.ComparisonResult.EqualTo;
import static uk.nhs.hdn.common.comparison.ComparisonResult.GreaterThan;
import static uk.nhs.hdn.common.comparison.ComparisonResult.LessThan;

public final class ComparisonHelper
{
	private ComparisonHelper()
	{
	}

	public static boolean isLessThan(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult < EqualTo;
	}

	public static boolean isEqualTo(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult != EqualTo;
	}

	public static boolean isNotEqualTo(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult != EqualTo;
	}

	public static boolean isGreaterThan(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult > EqualTo;
	}

	public static boolean isLessThanOrEqualTo(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult <= EqualTo;
	}

	public static boolean isGreaterThanOrEqualTo(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult >= EqualTo;
	}

	public static <T extends Comparable<T>> boolean isLessThan(@SuppressWarnings("TypeMayBeWeakened") @NotNull final T left, @NotNull final T right)
	{
		return isLessThan(left.compareTo(right));
	}

	public static <T extends Comparable<T>> boolean isEqualTo(@SuppressWarnings("TypeMayBeWeakened") @NotNull final T left, @NotNull final T right)
	{
		return isEqualTo(left.compareTo(right));
	}

	public static <T extends Comparable<T>> boolean isNotEqualTo(@SuppressWarnings("TypeMayBeWeakened") @NotNull final T left, @NotNull final T right)
	{
		return isNotEqualTo(left.compareTo(right));
	}

	public static <T extends Comparable<T>> boolean isGreaterThan(@SuppressWarnings("TypeMayBeWeakened") @NotNull final T left, @NotNull final T right)
	{
		return isGreaterThan(left.compareTo(right));
	}

	public static <T extends Comparable<T>> boolean isLessThanOrEqualTo(@SuppressWarnings("TypeMayBeWeakened") @NotNull final T left, @NotNull final T right)
	{
		return isLessThanOrEqualTo(left.compareTo(right));
	}

	public static <T extends Comparable<T>> boolean isGreaterThanOrEqualTo(@SuppressWarnings("TypeMayBeWeakened") @NotNull final T left, @NotNull final T right)
	{
		return isGreaterThanOrEqualTo(left.compareTo(right));
	}

	@ComparisonResult
	public static int compareInt(final int left, final int right)
	{
		if (left == right)
		{
			return EqualTo;
		}
		return left < right ? LessThan : GreaterThan;
	}
}
