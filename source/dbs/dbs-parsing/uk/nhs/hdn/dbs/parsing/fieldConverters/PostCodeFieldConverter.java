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

package uk.nhs.hdn.dbs.parsing.fieldConverters;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.CouldNotConvertFieldValueException;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.FieldConverter;
import uk.nhs.hdn.common.postCodes.PostCode;

import static uk.nhs.hdn.common.postCodes.AbstractPostCode.valueOf;

public final class PostCodeFieldConverter implements FieldConverter<PostCode>
{
	@NotNull
	public static final FieldConverter<?> PostCodeFieldConverterInstance = new PostCodeFieldConverter();

	private PostCodeFieldConverter()
	{
	}

	@NotNull
	@Override
	public PostCode convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		try
		{
			return valueOf(fieldValue);
		}
		catch(RuntimeException e)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, PostCode.class, e);
		}
	}
}
