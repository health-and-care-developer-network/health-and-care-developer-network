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

package uk.nhs.hdn.pseudonymisation.client;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.common.postCodes.AbstractPostCode;
import uk.nhs.hdn.number.NhsNumber;

public enum DataKind
{
	post_code
	{
		@NotNull
		@Override
		public Normalisable parse(@NotNull final String value)
		{
			return AbstractPostCode.valueOf(value);
		}
	},
	nhs_number
	{
		@NotNull
		@Override
		public Normalisable parse(@NotNull final String value)
		{
			return NhsNumber.valueOf(value);
		}
	},
	;

	@NotNull
	public abstract Normalisable parse(@NotNull final String value);
}
