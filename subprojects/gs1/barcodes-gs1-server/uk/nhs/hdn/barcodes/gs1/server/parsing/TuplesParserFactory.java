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
import uk.nhs.hdn.common.parsers.ParserFactory;
import uk.nhs.hdn.common.parsers.separatedValueParsers.TabSeparatedValueParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.StringNonMandatoryPrefixFieldParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.ToDomainSeparatedValueParseEventHandler;

public final class TuplesParserFactory implements ParserFactory
{
	private final Gs1CompanyPrefixResourceStateSnapshotUser gs1CompanyPrefixResourceStateSnapshotUser;

	public TuplesParserFactory(@NotNull final Gs1CompanyPrefixResourceStateSnapshotUser gs1CompanyPrefixResourceStateSnapshotUser)
	{
		this.gs1CompanyPrefixResourceStateSnapshotUser = gs1CompanyPrefixResourceStateSnapshotUser;
	}

	@NotNull
	@Override
	public Parser parser()
	{
		return new TabSeparatedValueParser
		(
			new ToDomainSeparatedValueParseEventHandler<>
			(
				new Gs1CompanyPrefixResourceStateSnapshotLinesParser
				(
					gs1CompanyPrefixResourceStateSnapshotUser
				),

				new TupleLineParser(),

				new Gs1CompanyPrefixFieldParser(),
				new NonEmptyStringFieldParser(),
				new NonEmptyStringFieldParser(),

				// PostCode
				new StringNonMandatoryPrefixFieldParser()
			)
		);
	}
}
