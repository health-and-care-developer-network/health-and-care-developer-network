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

package uk.nhs.hdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.TabSeparatedValueParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.StringNonMandatoryPrefixFieldParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.ToDomainSeparatedValueParseEventHandler;

import static uk.nhs.hdn.barcodes.gs1.server.parsing.Gs1CompanyPrefixFieldParser.Gs1CompanyPrefixFieldParserInstance;
import static uk.nhs.hdn.barcodes.gs1.server.parsing.TupleLineParser.TupleLineParserInstance;
import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser.NonEmptyStringFieldParserInstance;

public final class TuplesParserFactory
{
	private TuplesParserFactory()
	{
	}

	@NotNull
	public static Parser tuplesParser(@NotNull final Gs1CompanyPrefixResourceStateSnapshotUser gs1CompanyPrefixResourceStateSnapshotUser)
	{
		return new TabSeparatedValueParser<>
		(
			new ToDomainSeparatedValueParseEventHandler<>
			(
				new Gs1CompanyPrefixResourceStateSnapshotLinesParser(gs1CompanyPrefixResourceStateSnapshotUser),

				TupleLineParserInstance,

				Gs1CompanyPrefixFieldParserInstance,
				NonEmptyStringFieldParserInstance,
				NonEmptyStringFieldParserInstance,

				// PostCode
				new StringNonMandatoryPrefixFieldParser(true)
			)
		);
	}
}
