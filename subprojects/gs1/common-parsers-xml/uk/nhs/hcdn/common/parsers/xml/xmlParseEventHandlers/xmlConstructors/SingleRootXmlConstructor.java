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
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.SingleRootValueHolder;
import uk.nhs.hcdn.common.tuples.Pair;

public final class SingleRootXmlConstructor<V> extends AbstractRootXmlConstructor<V,V>
{
	@SafeVarargs
	@NotNull
	public static <V, W extends V> SingleRootXmlConstructor<V> rootSchemaFor(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final Class<V> returnsType, @NotNull final Class<W> implementsType, @NotNull final Pair<String, MissingFieldXmlConstructor<?, ?>>... xmlConstructorsForFields)
	{
		return root(rootNodeName, shouldPreserveWhitespace, new JavaObjectXmlConstructor<>(returnsType, implementsType, xmlConstructorsForFields));
	}

	@NotNull
	public static <V> SingleRootXmlConstructor<V> root(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor)
	{
		return new SingleRootXmlConstructor<>(rootNodeName, shouldPreserveWhitespace, xmlConstructor);
	}

	public SingleRootXmlConstructor(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor)
	{
		super(rootNodeName, shouldPreserveWhitespace, xmlConstructor, xmlConstructor.type());
	}

	@NotNull
	@Override
	public SingleRootValueHolder<V> start()
	{
		return new SingleRootValueHolder<>();
	}
}
