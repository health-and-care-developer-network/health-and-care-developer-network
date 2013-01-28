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
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;

import java.util.List;

public class CollectToArrayObjectsOnlyForElementsArrayConstructor<X> extends AbstractObjectsOnlyForElementsArrayConstructor<X>
{
	@NotNull
	private final ArrayCreator<X> arrayCreator;
	@NotNull
	private final ObjectConstructor<?> objectConstructor;

	public CollectToArrayObjectsOnlyForElementsArrayConstructor(final boolean nullPermitted, @NotNull final ArrayCreator<X> arrayCreator, @NotNull final ObjectConstructor<?> objectConstructor)
	{
		super(arrayCreator.type(), nullPermitted);
		this.arrayCreator = arrayCreator;
		this.objectConstructor = objectConstructor;
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor(final int index)
	{
		return objectConstructor;
	}

	@Override
	public X[] collect(@NotNull final List<X> collector)
	{
		return collector.toArray(arrayCreator.newInstance1(collector.size()));
	}
}
