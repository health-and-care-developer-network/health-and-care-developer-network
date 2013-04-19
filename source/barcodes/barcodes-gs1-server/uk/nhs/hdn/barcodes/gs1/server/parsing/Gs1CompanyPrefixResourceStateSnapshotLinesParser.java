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
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.barcodes.gs1.server.Gs1CompanyPrefixResourceStateSnapshot;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.parsers.separatedValueParsers.linesParsers.LinesParser;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.ArrayList;
import java.util.List;

import static uk.nhs.hdn.common.GregorianCalendarHelper.utc;

public final class Gs1CompanyPrefixResourceStateSnapshotLinesParser extends AbstractToString implements LinesParser<Tuple, List<Tuple>>
{
	@NotNull
	private final Gs1CompanyPrefixResourceStateSnapshotUser gs1CompanyPrefixResourceStateSnapshotUser;

	public Gs1CompanyPrefixResourceStateSnapshotLinesParser(@NotNull final Gs1CompanyPrefixResourceStateSnapshotUser gs1CompanyPrefixResourceStateSnapshotUser)
	{
		this.gs1CompanyPrefixResourceStateSnapshotUser = gs1CompanyPrefixResourceStateSnapshotUser;
	}

	@NotNull
	@Override
	public List<Tuple> newParsedLines()
	{
		return new ArrayList<>(10);
	}

	@Override
	public void parse(@MillisecondsSince1970 final long lastModified, @NotNull final List<Tuple> parsedLines)
	{
		final Tuple[] tuples = parsedLines.toArray(new Tuple[parsedLines.size()]);

		final Gs1CompanyPrefixResourceStateSnapshot gs1CompanyPrefixResourceStateSnapshot = new Gs1CompanyPrefixResourceStateSnapshot(utc(lastModified), tuples);

		gs1CompanyPrefixResourceStateSnapshotUser.use(gs1CompanyPrefixResourceStateSnapshot);
	}
}
