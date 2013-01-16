/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.Parser;
import uk.nhs.hcdn.common.parsers.ParserFactory;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.TabSeparatedValueParser;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.StringNonMandatoryPrefixFieldParser;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.ToDomainSeparatedValueParseEventHandler;

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
