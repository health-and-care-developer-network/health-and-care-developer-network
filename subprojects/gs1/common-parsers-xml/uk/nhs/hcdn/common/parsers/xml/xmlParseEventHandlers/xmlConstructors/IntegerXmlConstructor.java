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

import org.jetbrains.annotations.NotNull;

import static java.lang.Integer.parseInt;

public final class IntegerXmlConstructor extends TextXmlConstructor<Integer>
{
	@NotNull
	public static final XmlConstructor<?, ?> IntegerXmlConstructorInstance = new IntegerXmlConstructor();

	private IntegerXmlConstructor()
	{
		super(0);
	}

	@NotNull
	@Override
	protected Integer convert(@NotNull final String text)
	{
		return parseInt(text);
	}

	@NotNull
	@Override
	public Class<Integer> type()
	{
		return Integer.class;
	}
}
