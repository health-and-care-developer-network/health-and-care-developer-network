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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.common.reflection.toString.delegates.Delegate;
import uk.nhs.hdn.common.reflection.toString.delegates.StaticMethodDelegate;

import static uk.nhs.hdn.common.reflection.toString.delegates.ConstructorDelegate.constructorDelegate;

public final class ConvertUsingDelegateListCollectingStringArrayConstructor<X> extends AbstractListCollectingStringArrayConstructor<X>
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <X> ConvertUsingDelegateListCollectingStringArrayConstructor<X> convertUsingDelegateListCollectingStringArrayConstructor(@NotNull final ArrayCreator<X> arrayCreator, @NotNull final Class<?> staticMethodClass, @NotNull final String staticMethodName)
	{
		return new ConvertUsingDelegateListCollectingStringArrayConstructor<>(arrayCreator, StaticMethodDelegate.<X>staticMethodDelegate(staticMethodClass, staticMethodName, String.class));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <X> ConvertUsingDelegateListCollectingStringArrayConstructor<X> convertUsingDelegateListCollectingStringArrayConstructor(@NotNull final ArrayCreator<X> arrayCreator, @NotNull final Class<X> classToConstruct)
	{
		return new ConvertUsingDelegateListCollectingStringArrayConstructor<>(arrayCreator, constructorDelegate(classToConstruct, String.class));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <X> ConvertUsingDelegateListCollectingStringArrayConstructor<X> convertUsingDelegateListCollectingStringArrayConstructor(@NotNull final ArrayCreator<X> arrayCreator, @NotNull final Delegate<X> delegate)
	{
		return new ConvertUsingDelegateListCollectingStringArrayConstructor<>(arrayCreator, delegate);
	}

	@NotNull private final Delegate<X> delegate;

	public ConvertUsingDelegateListCollectingStringArrayConstructor(@NotNull final ArrayCreator<X> arrayCreator, @NotNull final Delegate<X> delegate)
	{
		super(arrayCreator);
		this.delegate = delegate;
	}

	@Nullable
	@Override
	protected X convertFromString(@NotNull final String value)
	{
		return delegate.invoke(value);
	}
}
