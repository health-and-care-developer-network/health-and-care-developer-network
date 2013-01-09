package org.gov.data.nhs.hcdn.barcodes.gs1.restrictedCirculation;

import org.gov.data.nhs.hcdn.barcodes.Digit;
import org.gov.data.nhs.hcdn.common.tuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.gov.data.nhs.hcdn.common.IntegerHelper.power;
import static org.gov.data.nhs.hcdn.barcodes.Digit.Zero;
import static org.gov.data.nhs.hcdn.barcodes.gs1.restrictedCirculation.RestrictedCirculationNumber.from;

public enum Gs1PrefixAssignment
{
	GS1MemberOrganisationDefined(from(0, 2, 0).to(0, 2, 9), from(0, 4, 0).to(0, 4, 9), from(2, 0, 0).to(2, 9, 9)),
	Coupons(from(0, 5, 0).to(0, 5, 9), from(9, 9, 0).to(9, 9, 9)),
	ISSN(from(9, 7, 7)),
	ISBN(from(9, 7, 8).to(9, 7, 9))
	{
		@SuppressWarnings("RefusedBequest")
		@Override
		public boolean isISMN(@NotNull final Digit fourth)
		{
			return fourth == Zero;
		}
	},
	RefundReceipts(from(9, 8, 0).to(9, 8, 0)),
	CommonCurrentCoupons(from(9, 8, 1).to(9, 8 ,2)),
	;

	@SuppressWarnings({"ClassWithoutConstructor", "UtilityClassWithoutPrivateConstructor"})
	private static final class CompilerWorkaround
	{
		private static final int SizeOfDigit = 10;
		private static final Gs1PrefixAssignment[] Index = new Gs1PrefixAssignment[power(SizeOfDigit, 3)];
	}

	Gs1PrefixAssignment(@NotNull final RestrictedCirculationNumber single)
	{
		CompilerWorkaround.Index[single.to0To999()] = this;
	}

	@SafeVarargs
	Gs1PrefixAssignment(@NotNull final Pair<RestrictedCirculationNumber>... pairs)
	{
		if (pairs.length == 0)
		{
			throw new IllegalArgumentException("pairs must be > 0");
		}

		for (final Pair<RestrictedCirculationNumber> pair : pairs)
		{
			final RestrictedCirculationNumber lowerBoundInclusive = pair.a;
			final int lowerBoundIndex = lowerBoundInclusive.to0To999();

			final RestrictedCirculationNumber upperBoundInclusive = pair.a;
			final int upperBoundIndex = upperBoundInclusive.to0To999();

			for(int index = lowerBoundIndex; index <= upperBoundIndex; index++)
			{
				if (CompilerWorkaround.Index[index] != null)
				{
					throw new IllegalStateException("ranges overlap");
				}
				CompilerWorkaround.Index[index] = this;
			}
		}
	}

	public boolean isISMN(@NotNull final Digit fourth)
	{
		return false;
	}

	@Nullable
	public static Gs1PrefixAssignment restrictedCirculationNumberRangeKind(@NotNull final RestrictedCirculationNumber restrictedCirculationNumber)
	{
		return CompilerWorkaround.Index[restrictedCirculationNumber.to0To999()];
	}
}
