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

package uk.nhs.hdn.ihe.xds.builders;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.naming.ActualName;

public enum EncodingType implements ActualName
{
	identity,
	gzip,
	compress(true),
	deflate(true),
	exi,
	pack200_gzip("pack200-gzip"),
	;

	@NotNull @NonNls private final String actualName;
	public final boolean isLegacy;

	EncodingType()
	{
		actualName = name();
		isLegacy = false;
	}

	EncodingType(final boolean isLegacy)
	{
		actualName = name();
		this.isLegacy = isLegacy;
	}

	EncodingType(@NotNull @NonNls final String actualName)
	{
		this.actualName = actualName;
		isLegacy = false;
	}

	@Override
	public String toString()
	{
		return actualName;
	}

	@NonNls
	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}
}
