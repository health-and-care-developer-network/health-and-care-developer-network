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

public final class LongArrayCreator extends AbstractArrayCreator<Long>
{
	@NotNull
	public static final ArrayCreator<Long> LongArray = new LongArrayCreator();

	private LongArrayCreator()
	{
		super(Long.class, Long[].class);
	}

	@NotNull
	@Override
	public Long[] newInstance1(final int size)
	{
		return new Long[size];
	}

	@NotNull
	@Override
	public Long[][] newInstance2(final int size)
	{
		return new Long[size][];
	}
}
