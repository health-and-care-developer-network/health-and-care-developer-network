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

package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.ArrayRootValueHolder;
import uk.nhs.hcdn.common.tuples.Pair;

public final class ArrayRootXmlConstructor<V> extends AbstractRootXmlConstructor<V, V[]>
{
	@NotNull
	private final ArrayCreator<V> arrayCreator;

	@SafeVarargs
	@NotNull
	public static <V> ArrayRootXmlConstructor<V> rootSchemaFor(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final ArrayCreator<V> arrayCreator, @NotNull final Pair<String, MissingFieldXmlConstructor<?, ?>>... xmlConstructorsForFields)
	{
		@NotNull final Class<V> returnsType = arrayCreator.type();
		return root(rootNodeName, shouldPreserveWhitespace, new JavaObjectXmlConstructor<>(returnsType, returnsType, xmlConstructorsForFields), arrayCreator);
	}

	@NotNull
	public static <V> ArrayRootXmlConstructor<V> root(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor, @NotNull final ArrayCreator<V> arrayCreator)
	{
		return new ArrayRootXmlConstructor<>(rootNodeName, shouldPreserveWhitespace, xmlConstructor, arrayCreator);
	}

	public ArrayRootXmlConstructor(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor, @NotNull final ArrayCreator<V> arrayCreator)
	{
		super(rootNodeName, shouldPreserveWhitespace, xmlConstructor, arrayCreator.arrayType());
		this.arrayCreator = arrayCreator;
	}

	@NotNull
	@Override
	public ArrayRootValueHolder<V> start()
	{
		return new ArrayRootValueHolder<>(arrayCreator);
	}
}
