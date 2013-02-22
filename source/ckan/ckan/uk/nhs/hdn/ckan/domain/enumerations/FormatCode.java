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

package uk.nhs.hdn.ckan.domain.enumerations;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.Format;

import java.util.HashMap;
import java.util.Map;

import static java.util.Locale.ENGLISH;

public enum FormatCode
{
	CSV,
	Excel
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("XLS", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("XLSX", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("XLSM", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("XLB", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("XSL", this); //NON-NLS
		}
	},
	RDF,
	XML
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("XXML", this); //NON-NLS
		}
	},
	Word
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("DOC", this); //NON-NLS
		}
	},
	pdf,
	HTML
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("HTM", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("HMTL", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("WEBPAGE", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("WEBSITE", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("WEB", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("URL", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("ASPX", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("ASP", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("PHP", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("AXD", this); //NON-NLS
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("API", this); //NON-NLS
		}
	},
	ViewService
	{
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("VIEWSERICE", this); //NON-NLS
		}
	},
	WMS,
	txt,
	ppt
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("PPTX", this); //NON-NLS
		}
	},
	NetCDF,
	RSS,
	KML,
	GeoRSS,
	iCal
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("ICALENDAR", this); //NON-NLS
		}
	},
	RDFa,
	RTF,
	JSON,
	ODS,
	IATI,
	ODP,
	SHP,
	EXE,
	SPSS
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("SAV", this); //NON-NLS
		}
	},
	JPG,
	GIF,
	SKOS,

	database
	{
		// Fix to accommodate corrupt data
		{
			//noinspection ThisEscapedInObjectConstruction
			CompilerWorkaround.Index.put("SQL", this); //NON-NLS
		}
	},
	zip,
	gz,

	UnknownFormatCode,
	;

	private static final class CompilerWorkaround
	{
		@NotNull private static final Map<String, FormatCode> Index = new HashMap<>(50);
	}

	@NotNull @NonNls public final String normalisedForm;
	@SuppressWarnings("NonSerializableFieldInSerializableClass") @NotNull public final Format singleFormatCodeFormat;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	FormatCode()
	{
		normalisedForm = name().toUpperCase(ENGLISH);
		if (CompilerWorkaround.Index.put(normalisedForm, this) != null)
		{
			throw new IllegalArgumentException("Already added");
		}
		singleFormatCodeFormat = new Format(this);
	}

	@NotNull
	public Format with(@NotNull final FormatCode formatCode)
	{
		return new Format(this, formatCode);
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static FormatCode formatCode(@NotNull final String formatCodeUpperCased)
	{
		return CompilerWorkaround.Index.get(formatCodeUpperCased);
	}
}
