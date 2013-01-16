/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.Tuple;
import uk.nhs.hcdn.barcodes.gs1.server.Gs1CompanyPrefixResourceStateSnapshot;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.linesParsers.LinesParser;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.util.List;

import static uk.nhs.hcdn.common.GregorianCalendarHelper.utc;

public final class Gs1CompanyPrefixResourceStateSnapshotLinesParser extends AbstractToString implements LinesParser<Tuple>
{
	@NotNull
	private final Gs1CompanyPrefixResourceStateSnapshotUser gs1CompanyPrefixResourceStateSnapshotUser;

	public Gs1CompanyPrefixResourceStateSnapshotLinesParser(@NotNull final Gs1CompanyPrefixResourceStateSnapshotUser gs1CompanyPrefixResourceStateSnapshotUser)
	{
		this.gs1CompanyPrefixResourceStateSnapshotUser = gs1CompanyPrefixResourceStateSnapshotUser;
	}

	@Override
	public void parse(@MillisecondsSince1970 final long lastModified, @NotNull final List<Tuple> lines)
	{
		final Tuple[] tuples = lines.toArray(new Tuple[lines.size()]);

		final Gs1CompanyPrefixResourceStateSnapshot gs1CompanyPrefixResourceStateSnapshot = new Gs1CompanyPrefixResourceStateSnapshot(utc(lastModified), tuples);

		gs1CompanyPrefixResourceStateSnapshotUser.use(gs1CompanyPrefixResourceStateSnapshot);
	}
}
