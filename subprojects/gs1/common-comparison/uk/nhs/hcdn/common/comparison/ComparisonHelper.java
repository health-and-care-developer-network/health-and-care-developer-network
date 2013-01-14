/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.comparison;

import org.jetbrains.annotations.NotNull;

import static uk.nhs.hcdn.common.comparison.ComparisonResult.EqualTo;
import static uk.nhs.hcdn.common.comparison.ComparisonResult.GreaterThan;
import static uk.nhs.hcdn.common.comparison.ComparisonResult.LessThan;

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
