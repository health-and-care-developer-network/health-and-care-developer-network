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

package uk.nhs.hdn.common.hazelcast.hazelcastSerialisationHolders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

@SuppressWarnings("serial")
public abstract class AbstractHazelcastSerialisationHolder<V> implements HazelcastSerialisationHolder<V>
{
	@SuppressWarnings("NonSerializableFieldInSerializableClass") @Nullable protected V heldValue;

	protected AbstractHazelcastSerialisationHolder()
	{
		heldValue = null;
	}

	@SuppressWarnings("NullableProblems")
	protected AbstractHazelcastSerialisationHolder(@NotNull final V heldValue)
	{
		this.heldValue = heldValue;
	}

	@Override
	@NotNull
	public final V heldValue()
	{
		assert heldValue != null;
		return heldValue;
	}

	@SuppressWarnings("NonFinalFieldReferenceInEquals")
	@Override
	public final boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final AbstractHazelcastSerialisationHolder<?> that = (AbstractHazelcastSerialisationHolder<?>) obj;

		if (heldValue != null ? !heldValue.equals(that.heldValue) : that.heldValue != null)
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings("NonFinalFieldReferencedInHashCode")
	@Override
	public final int hashCode()
	{
		return heldValue != null ? heldValue.hashCode() : 0;
	}

	@Override
	public final String toString()
	{
		return format(ENGLISH, "%1$s(%2$s)", getClass().getSimpleName(), heldValue == null ? "null" : heldValue);
	}
}
