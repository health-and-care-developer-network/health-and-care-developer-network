package uk.nhs.hcdn.common.comparison;

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
		return comparisonResult < ComparisonResult.EqualTo;
	}

	public static boolean isNotEqualTo(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult != ComparisonResult.EqualTo;
	}

	public static boolean isGreaterThan(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult > ComparisonResult.EqualTo;
	}

	@ComparisonResult
	public static int compareInt(final int left, final int right)
	{
		if (left == right)
		{
			return ComparisonResult.EqualTo;
		}
		return left < right ? ComparisonResult.LessThan : ComparisonResult.GreaterThan;
	}
}
