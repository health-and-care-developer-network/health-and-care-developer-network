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

package uk.nhs.hdn.dbs.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.unknown.AbstractIsUnknown;
import uk.nhs.hdn.dbs.LocalPatientIdentifier;
import uk.nhs.hdn.dbs.RecordType;

public abstract class AbstractResponseBody extends AbstractIsUnknown implements ResponseBody
{
	@NotNull
	public final RecordType responseCode;
	@NotNull
	public final LocalPatientIdentifier localPatientIdentifier;
	public final int numberOfMultipleMatches;

	protected AbstractResponseBody(@NotNull final RecordType responseCode, @NotNull final LocalPatientIdentifier localPatientIdentifier, final int numberOfMultipleMatches)
	{
		super(responseCode.doesNotHaveMatches);
		this.responseCode = responseCode;
		this.localPatientIdentifier = localPatientIdentifier;
		this.numberOfMultipleMatches = numberOfMultipleMatches;
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
		if (!super.equals(obj))
		{
			return false;
		}

		final AbstractResponseBody that = (AbstractResponseBody) obj;

		if (numberOfMultipleMatches != that.numberOfMultipleMatches)
		{
			return false;
		}
		if (!localPatientIdentifier.equals(that.localPatientIdentifier))
		{
			return false;
		}
		if (responseCode != that.responseCode)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + responseCode.hashCode();
		result = 31 * result + localPatientIdentifier.hashCode();
		result = 31 * result + numberOfMultipleMatches;
		return result;
	}
}
