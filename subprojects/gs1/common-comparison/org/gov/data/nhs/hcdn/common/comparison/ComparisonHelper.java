package org.gov.data.nhs.hcdn.common.comparison;

import static org.gov.data.nhs.hcdn.common.comparison.ComparisonResult.EqualTo;
import static org.gov.data.nhs.hcdn.common.comparison.ComparisonResult.GreaterThan;
import static org.gov.data.nhs.hcdn.common.comparison.ComparisonResult.LessThan;

public final class ComparisonHelper
{
	private ComparisonHelper()
	{
	}

	public static boolean isLessThan(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult < EqualTo;
	}

	public static boolean isNotEqualTo(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult != EqualTo;
	}

	public static boolean isGreaterThan(@ComparisonResult final int comparisonResult)
	{
		return comparisonResult > EqualTo;
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
