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

package uk.nhs.hdn.ckan.domain.urls;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.unknown.AbstractIsUnknown;

import java.net.MalformedURLException;
import java.net.URL;

import static uk.nhs.hdn.ckan.domain.urls.UnknownUrl.UnknownUrlInstance;

public abstract class AbstractUrl extends AbstractIsUnknown implements Url
{
	@NotNull
	public static Url parseUrl(@Nullable final String value)
	{
		if (value == null || value.isEmpty())
		{
			return UnknownUrlInstance;
		}
		final URL url;

		try
		{
			url = new URL(value);
		}
		catch (MalformedURLException e)
		{
			return new InvalidUrl(value, e);
		}
		return new KnownUrl(url);
	}

	protected AbstractUrl(final boolean isUnknown)
	{
		super(isUnknown);
	}

	@Override
	public final void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseValue(serialiser);
		}
		catch (CouldNotSerialiseValueException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(stringValue());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}
}
