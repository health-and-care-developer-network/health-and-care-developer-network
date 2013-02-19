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

package uk.nhs.hdn.ckan.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.enumerations.Capacity;
import uk.nhs.hdn.ckan.domain.strings.Hash;
import uk.nhs.hdn.ckan.domain.strings.OpenId;
import uk.nhs.hdn.ckan.domain.uniqueNames.UserName;
import uk.nhs.hdn.ckan.domain.uuids.UserId;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

public final class User extends AbstractToString implements ValueSerialisable
{
	@Nullable public final OpenId openId;
	@Nullable @NonNls public final String about;
	@NotNull public final Capacity capacity;
	@NotNull public final UserName userName;
	@NotNull public final MicrosecondTimestamp created;
	@NotNull public final Hash emailHash;
	public final long numberOfEdits;
	public final long numberOfAdministeredPackages;
	@NotNull @NonNls public final String displayName;
	@NotNull @NonNls public final String fullName;
	@NotNull public final UserId id;

	@SuppressWarnings("ConstructorWithTooManyParameters")
	public User(@Nullable final OpenId openId, @Nullable @NonNls final String about, @NotNull final Capacity capacity, @NotNull final UserName userName, @NotNull final MicrosecondTimestamp created, @NotNull final Hash emailHash, final long numberOfEdits, final long numberOfAdministeredPackages, @NotNull @NonNls final String displayName, @NotNull @NonNls final String fullName, @NotNull final UserId id)
	{
		this.openId = openId;
		this.about = about;
		this.capacity = capacity;
		this.userName = userName;
		this.created = created;
		this.emailHash = emailHash;
		this.numberOfEdits = numberOfEdits;
		this.numberOfAdministeredPackages = numberOfAdministeredPackages;
		this.displayName = displayName;
		this.fullName = fullName;
		this.id = id;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(toString());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@SuppressWarnings({"ConditionalExpression", "FeatureEnvy", "OverlyComplexMethod"})
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

		final User user = (User) obj;

		if (numberOfAdministeredPackages != user.numberOfAdministeredPackages)
		{
			return false;
		}
		if (numberOfEdits != user.numberOfEdits)
		{
			return false;
		}
		if (about != null ? !about.equals(user.about) : user.about != null)
		{
			return false;
		}
		if (!created.equals(user.created))
		{
			return false;
		}
		if (!displayName.equals(user.displayName))
		{
			return false;
		}
		if (!emailHash.equals(user.emailHash))
		{
			return false;
		}
		if (!fullName.equals(user.fullName))
		{
			return false;
		}
		if (!id.equals(user.id))
		{
			return false;
		}
		if (openId != null ? !openId.equals(user.openId) : user.openId != null)
		{
			return false;
		}
		if (capacity != user.capacity)
		{
			return false;
		}
		if (!userName.equals(user.userName))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({"ConditionalExpression", "FeatureEnvy"})
	@Override
	public int hashCode()
	{
		int result = openId != null ? openId.hashCode() : 0;
		result = 31 * result + (about != null ? about.hashCode() : 0);
		result = 31 * result + capacity.hashCode();
		result = 31 * result + userName.hashCode();
		result = 31 * result + created.hashCode();
		result = 31 * result + emailHash.hashCode();
		result = 31 * result + (int) (numberOfEdits ^ (numberOfEdits >>> 32));
		result = 31 * result + (int) (numberOfAdministeredPackages ^ (numberOfAdministeredPackages >>> 32));
		result = 31 * result + displayName.hashCode();
		result = 31 * result + fullName.hashCode();
		result = 31 * result + id.hashCode();
		return result;
	}
}
