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
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hcdn.common.arrayCreators.AbstractArrayCreator;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.AbstractListCollectingLongNumberArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.util.List;

import static uk.nhs.hcdn.barcodes.Digit.digit;

public final class Gs1CompanyPrefixArrayConstructor extends AbstractListCollectingLongNumberArrayConstructor<Digit>
{
	@NotNull
	public static final ArrayConstructor<?> Gs1CompanyPrefixArrayConstructorInstance = new Gs1CompanyPrefixArrayConstructor();

	private Gs1CompanyPrefixArrayConstructor()
	{
		super(new AbstractArrayCreator<Digit>(Digit.class, Digit[].class)
		{
			@NotNull
			@Override
			public Digit[] newInstance1(final int size)
			{
				return new Digit[size];
			}

			@NotNull
			@Override
			public Digit[][] newInstance2(final int size)
			{
				return new Digit[size][];
			}
		});
	}

	@Override
	public void addConstantNumberValue(@NotNull final List<Digit> arrayCollector, final int index, final long value) throws SchemaViolationInvalidJsonException
	{
		final Digit digit;
		try
		{
			digit = digit(value);
		}
		catch (IllegalArgumentException e)
		{
			throw new SchemaViolationInvalidJsonException("digit is not between 0 and 9 inclusive", e);
		}
		arrayCollector.add(digit);
	}

	@Override
	@NotNull
	public Object collect(@NotNull final List<Digit> collector) throws SchemaViolationInvalidJsonException
	{
		final Digit[] collected = (Digit[]) super.collect(collector);
		return new Gs1CompanyPrefix(new Digits(false, collected));
	}
}
