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

package uk.nhs.hdn.common.postCodes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.postCodes.codes.inward.BritishForcesPostOfficeInwardCode;
import uk.nhs.hdn.common.postCodes.codes.inward.InwardCode;
import uk.nhs.hdn.common.postCodes.codes.inward.RoyalMailInwardCode;
import uk.nhs.hdn.common.postCodes.codes.outward.OutwardCode;
import uk.nhs.hdn.common.postCodes.codes.outward.RoyalMailOutwardCode;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.regex.Pattern.compile;
import static uk.nhs.hdn.common.postCodes.codes.outward.BritishForcesPostOfficeOutwardCode.BFPO;
import static uk.nhs.hdn.common.postCodes.codes.outward.BritishForcesPostOfficeOutwardCode.BritishForcesPostOfficeOutwardCodeInstance;

public abstract class AbstractPostCode<O extends OutwardCode<PostCode, I>, I extends InwardCode> extends AbstractToString implements PostCode
{
	private static final Pattern Space = compile(" ");

	@NotNull
	public static PostCode valueOf(@NotNull final String potentialPostCode)
	{
		final String[] codes = Space.split(potentialPostCode, 2);
		if (codes.length != 2)
		{
			throw new IllegalArgumentException(format(ENGLISH, "The potentialPostCode '%1$s' does not consist of two parts separated by a space", potentialPostCode));
		}

		final String outwardCode = codes[0];
		final String inwardCode = codes[1];

		if (outwardCode.equals(BFPO))
		{
			return BritishForcesPostOfficeOutwardCodeInstance.with(new BritishForcesPostOfficeInwardCode(inwardCode));
		}
		return new RoyalMailOutwardCode(outwardCode).with(new RoyalMailInwardCode(inwardCode));
	}

	@NotNull
	public final O outwardCode;

	@NotNull
	public final I inwardCode;

	protected AbstractPostCode(@NotNull final O outwardCode, @NotNull final I inwardCode)
	{
		this.outwardCode = outwardCode;
		this.inwardCode = inwardCode;
	}

	@NonNls
	@NotNull
	@Override
	public final String normalised()
	{
		return outwardCode.normalised() + ' ' + inwardCode.normalised();
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final AbstractPostCode<?, ?> that = (AbstractPostCode<?, ?>) obj;

		if (!inwardCode.equals(that.inwardCode))
		{
			return false;
		}
		if (!outwardCode.equals(that.outwardCode))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = outwardCode.hashCode();
		result = 31 * result + inwardCode.hashCode();
		return result;
	}
}
