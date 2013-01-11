package uk.nhs.hcdn.common.comparison;

import org.jetbrains.annotations.NotNull;

public interface ExtendedComparable<T> extends Comparable<T>
{
	boolean isLessThan(@NotNull final T right);

	boolean isEqualTo(@NotNull final T right);

	boolean isNotEqualTo(@NotNull final T right);

	boolean isGreaterThan(@NotNull final T right);

	boolean isLessThanOrEqualTo(@NotNull final T right);

	boolean isGreaterThanOrEqualTo(@NotNull final T right);
}
