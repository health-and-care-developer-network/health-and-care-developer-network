/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.AdditionalInformation;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.AdditionalInformationKey;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.Tuple;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.lineParsers.CouldNotParseLineException;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.lineParsers.LineParser;

import java.util.HashMap;

public final class TupleLineParser implements LineParser<Tuple>
{
	private static final int NumberOfInvariantConstructorFields = 3;
	@NotNull
	private static final AdditionalInformationKey[] AdditionalInformationKeys = AdditionalInformationKey.values();
	private static final int MaximumAdditionalInformationKeys = AdditionalInformationKeys.length;
	private static final int MaximumLength = NumberOfInvariantConstructorFields + MaximumAdditionalInformationKeys;

	@SuppressWarnings({"ClassExtendsConcreteCollection", "MethodCanBeVariableArityMethod"})
	@NotNull
	@Override
	public Tuple parse(final int lineIndex, @NotNull final Object[] parsedFields) throws CouldNotParseLineException
	{
		final int length = parsedFields.length;
		if (length < NumberOfInvariantConstructorFields)
		{
			throw new CouldNotParseLineException(lineIndex, "Not enough fields");
		}

		if (length > MaximumLength)
		{
			throw new CouldNotParseLineException(lineIndex, "Too many fields");
		}

		final int additionalFieldsLength = length - NumberOfInvariantConstructorFields;
		return new Tuple
		(
			(Gs1CompanyPrefix) parsedFields[0],
			(String) parsedFields[1],
			(String) parsedFields[2],
			new AdditionalInformation(new HashMap<AdditionalInformationKey, Object>(additionalFieldsLength)
			{{
				for(int index = 0; index < additionalFieldsLength; index++)
				{
					@Nullable final Object parsedField = parsedFields[index + NumberOfInvariantConstructorFields];
					if (parsedField == null)
					{
						continue;
					}
					final AdditionalInformationKey key = AdditionalInformationKeys[index];
					put(key, parsedField);
				}
			}})
		);
	}
}
