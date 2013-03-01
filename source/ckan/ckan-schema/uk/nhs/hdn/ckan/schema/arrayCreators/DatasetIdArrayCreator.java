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

package uk.nhs.hdn.ckan.schema.arrayCreators;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.common.arrayCreators.AbstractArrayCreator;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;

public final class DatasetIdArrayCreator extends AbstractArrayCreator<DatasetId>
{
	@NotNull
	public static final ArrayCreator<DatasetId> DatasetIdArray = new DatasetIdArrayCreator();

	private DatasetIdArrayCreator()
	{
		super(DatasetId.class, DatasetId[].class);
	}

	@NotNull
	@Override
	public DatasetId[] newInstance1(final int size)
	{
		return new DatasetId[size];
	}

	@NotNull
	@Override
	public DatasetId[][] newInstance2(final int size)
	{
		return new DatasetId[size][];
	}
}
