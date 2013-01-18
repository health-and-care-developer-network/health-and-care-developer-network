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

package uk.nhs.hcdn.barcodes.gs1.client.schema;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.Tuple;
import uk.nhs.hcdn.common.arrayCreators.AbstractArrayCreator;
import uk.nhs.hcdn.common.arrayCreators.ArrayCreator;

public final class TupleArrayCreator extends AbstractArrayCreator<Tuple>
{
	@NotNull
	public static final ArrayCreator<Tuple> TupleArray = new TupleArrayCreator();

	private TupleArrayCreator()
	{
		super(Tuple.class, Tuple[].class);
	}

	@NotNull
	@Override
	public Tuple[] newInstance1(final int size)
	{
		return new Tuple[size];
	}

	@NotNull
	@Override
	public Tuple[][] newInstance2(final int size)
	{
		return new Tuple[size][];
	}
}
