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

package uk.nhs.hdn.dbs.response.practiceAndAddressData;

import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.postCodes.PostCode;
import uk.nhs.hdn.common.unknown.AbstractIsUnknown;
import uk.nhs.hdn.dbs.AddressLine;
import uk.nhs.hdn.dbs.SpineDirectoryServiceGpPracticeCode;

public final class KnownPracticeAndAddressData extends AbstractIsUnknown implements PracticeAndAddressData
{
	@Nullable
	public final AddressLine returnedAddressLine1;
	@Nullable
	public final AddressLine returnedAddressLine2;
	@Nullable
	public final AddressLine returnedAddressLine3;
	@Nullable
	public final AddressLine returnedAddressLine4;
	@Nullable
	public final AddressLine returnedAddressLine5;
	@Nullable
	public final PostCode returnedPostCode;
	@Nullable
	public final SpineDirectoryServiceGpPracticeCode returnedRegisteredGpPracticeCode;

	public KnownPracticeAndAddressData(@Nullable final AddressLine returnedAddressLine1, @Nullable final AddressLine returnedAddressLine2, @Nullable final AddressLine returnedAddressLine3, @Nullable final AddressLine returnedAddressLine4, @Nullable final AddressLine returnedAddressLine5, @Nullable final PostCode returnedPostCode, @Nullable final SpineDirectoryServiceGpPracticeCode returnedRegisteredGpPracticeCode)
	{
		super(false);
		this.returnedAddressLine1 = returnedAddressLine1;
		this.returnedAddressLine2 = returnedAddressLine2;
		this.returnedAddressLine3 = returnedAddressLine3;
		this.returnedAddressLine4 = returnedAddressLine4;
		this.returnedAddressLine5 = returnedAddressLine5;
		this.returnedPostCode = returnedPostCode;
		this.returnedRegisteredGpPracticeCode = returnedRegisteredGpPracticeCode;
	}

	@SuppressWarnings({"OverlyComplexMethod", "FeatureEnvy"})
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
		if (!super.equals(obj))
		{
			return false;
		}

		final KnownPracticeAndAddressData that = (KnownPracticeAndAddressData) obj;

		if (!(returnedAddressLine1 != null && returnedAddressLine1.equals(that.returnedAddressLine1)))
		{
			return false;
		}
		if (!(returnedAddressLine2 != null && returnedAddressLine2.equals(that.returnedAddressLine2)))
		{
			return false;
		}
		if (!(returnedAddressLine3 != null && returnedAddressLine3.equals(that.returnedAddressLine3)))
		{
			return false;
		}
		if (!(returnedAddressLine4 != null && returnedAddressLine4.equals(that.returnedAddressLine4)))
		{
			return false;
		}
		if (!(returnedAddressLine5 != null && returnedAddressLine5.equals(that.returnedAddressLine5)))
		{
			return false;
		}
		if (!(returnedPostCode != null && returnedPostCode.equals(that.returnedPostCode)))
		{
			return false;
		}
		if (!(returnedRegisteredGpPracticeCode != null && returnedRegisteredGpPracticeCode.equals(that.returnedRegisteredGpPracticeCode)))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({"FeatureEnvy", "ConditionalExpression", "MethodWithMoreThanThreeNegations"})
	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + (returnedAddressLine1 != null ? returnedAddressLine1.hashCode() : 0);
		result = 31 * result + (returnedAddressLine2 != null ? returnedAddressLine2.hashCode() : 0);
		result = 31 * result + (returnedAddressLine3 != null ? returnedAddressLine3.hashCode() : 0);
		result = 31 * result + (returnedAddressLine4 != null ? returnedAddressLine4.hashCode() : 0);
		result = 31 * result + (returnedAddressLine5 != null ? returnedAddressLine5.hashCode() : 0);
		result = 31 * result + (returnedPostCode != null ? returnedPostCode.hashCode() : 0);
		result = 31 * result + (returnedRegisteredGpPracticeCode != null ? returnedRegisteredGpPracticeCode.hashCode() : 0);
		return result;
	}
}
