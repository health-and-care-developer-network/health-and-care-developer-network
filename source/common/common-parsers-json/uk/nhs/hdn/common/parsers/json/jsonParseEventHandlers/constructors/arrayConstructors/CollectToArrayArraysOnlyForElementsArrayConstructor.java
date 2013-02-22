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
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.util.List;

public class CollectToArrayArraysOnlyForElementsArrayConstructor<X> extends AbstractArraysOnlyForElementsArrayConstructor<X>
{
	@NotNull
	private final ArrayCreator<X> arrayCreator;
	@NotNull
	private final ArrayConstructor<?> arrayConstructor;

	public CollectToArrayArraysOnlyForElementsArrayConstructor(final boolean nullPermitted, @NotNull final ArrayCreator<X> arrayCreator, @NotNull final ArrayConstructor<?> arrayConstructor)
	{
		super(arrayCreator.type(), nullPermitted);
		this.arrayCreator = arrayCreator;
		this.arrayConstructor = arrayConstructor;
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayConstructor(final int index) throws SchemaViolationInvalidJsonException
	{
		return arrayConstructor;
	}

	@Override
	public X[] collect(@NotNull final List<X> collector)
	{
		return collector.toArray(arrayCreator.newInstance1(collector.size()));
	}
}
