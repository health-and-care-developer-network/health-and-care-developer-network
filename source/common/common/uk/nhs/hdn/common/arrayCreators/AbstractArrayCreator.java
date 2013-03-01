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

package uk.nhs.hdn.common.arrayCreators;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public abstract class AbstractArrayCreator<V> implements ArrayCreator<V>
{
	@NotNull
	private final Class<V> type;
	@NotNull
	private final Class<V[]> arrayType;

	protected AbstractArrayCreator(@NotNull final Class<V> type, @NotNull final Class<V[]> arrayType)
	{
		this.type = type;
		this.arrayType = arrayType;
	}

	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s)", getClass().getSimpleName(), type.getSimpleName());
	}

	@NotNull
	@Override
	public final Class<V> type()
	{
		return type;
	}

	@NotNull
	@Override
	public final Class<V[]> arrayType()
	{
		return arrayType;
	}
}
