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

package uk.nhs.hcdn.dts.domain.identifiers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.util.Locale;

public abstract class AbstractIdentifier extends AbstractToString
{
	@NonNls
	@NotNull
	private final String value;

	public AbstractIdentifier(@NonNls @NotNull final String value, final int maximumCharacters)
	{
		// Daft - does not check for unpaired or badly paired surrogates!
		final int realLength = value.codePointCount(0, value.length());
		if (realLength > maximumCharacters)
		{
			throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Identifiers can not be more than %1$s characters long; %2$s was %3$s characters (we use code points)", maximumCharacters, value, value.length()));
		}
		this.value = value;
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

		final AbstractIdentifier that = (AbstractIdentifier) obj;

		if (!value.equals(that.value))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
