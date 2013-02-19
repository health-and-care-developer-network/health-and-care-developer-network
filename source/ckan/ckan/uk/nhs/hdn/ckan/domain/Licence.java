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

package uk.nhs.hdn.ckan.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.enumerations.Status;
import uk.nhs.hdn.ckan.domain.uniqueNames.LicenceId;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;

import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

@SuppressWarnings("OverlyCoupledClass")
public final class Licence extends AbstractToString implements Serialisable, MapSerialisable
{
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String statusField = "status";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String maintainerField = "maintainer";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String familyField = "family";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String titleField = "title";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String urlField = "url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String isGenericField = "is_generic";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String isOkdCompliantField = "is_okd_compliant";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String isOsiCompliantField = "is_osi_compliant";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String domainDataField = "domain_data";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String domainContentField = "domain_content";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String domainSoftwareField = "domain_software";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String idField = "id";

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		statusField,
		maintainerField,
		familyField,
		titleField,
		urlField,
		isGenericField,
		isOkdCompliantField,
		isOsiCompliantField,
		domainDataField,
		domainContentField,
		domainSoftwareField,
		idField
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf(statusField, 0),
		leaf(maintainerField, 1),
		leaf(familyField, 2),
		leaf(titleField, 3),
		leaf(urlField, 4),
		leaf(isGenericField, 5),
		leaf(isOkdCompliantField, 6),
		leaf(isOsiCompliantField, 7),
		leaf(domainDataField, 8),
		leaf(domainContentField, 9),
		leaf(domainSoftwareField, 10),
		leaf(idField, 11)
	);

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForLicences(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForLicences()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@NotNull public final Status status;
	@NotNull @NonNls public final String maintainer;
	@NotNull @NonNls public final String family;
	@NotNull @NonNls public final String title;
	@NotNull public final Url url;
	public final boolean isGeneric;
	public final boolean isOkdCompliant;
	public final boolean isOsiCompliant;
	public final boolean domainData;
	public final boolean domainContent;
	public final boolean domainSoftware;
	@NotNull public final LicenceId id;

	@SuppressWarnings("ConstructorWithTooManyParameters")
	public Licence(@NotNull final Status status, @NotNull @NonNls final String maintainer, @NotNull @NonNls final String family, @NotNull @NonNls final String title, @NotNull final Url url, final boolean isGeneric, final boolean isOkdCompliant, final boolean isOsiCompliant, final boolean domainData, final boolean domainContent, final boolean domainSoftware, @NotNull final LicenceId id)
	{
		this.status = status;
		this.maintainer = maintainer;
		this.family = family;
		this.title = title;
		this.url = url;
		this.isGeneric = isGeneric;
		this.isOkdCompliant = isOkdCompliant;
		this.isOsiCompliant = isOsiCompliant;
		this.domainData = domainData;
		this.domainContent = domainContent;
		this.domainSoftware = domainSoftware;
		this.id = id;
	}

	@Override
	public void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseMap(serialiser);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty(statusField, status);
			mapSerialiser.writeProperty(maintainerField, maintainer);
			mapSerialiser.writeProperty(familyField, family);
			mapSerialiser.writeProperty(titleField, title);
			mapSerialiser.writeProperty(urlField, url);
			mapSerialiser.writeProperty(isGenericField, isGeneric);
			mapSerialiser.writeProperty(isOkdCompliantField, isOkdCompliant);
			mapSerialiser.writeProperty(isOsiCompliantField, isOsiCompliant);
			mapSerialiser.writeProperty(domainDataField, domainData);
			mapSerialiser.writeProperty(domainContentField, domainContent);
			mapSerialiser.writeProperty(domainSoftwareField, domainSoftware);
			mapSerialiser.writeProperty(idField, id);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings({"FeatureEnvy", "OverlyComplexMethod"})
	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final Licence licence = (Licence) obj;

		if (domainContent != licence.domainContent)
		{
			return false;
		}
		if (domainData != licence.domainData)
		{
			return false;
		}
		if (domainSoftware != licence.domainSoftware)
		{
			return false;
		}
		if (isGeneric != licence.isGeneric)
		{
			return false;
		}
		if (isOkdCompliant != licence.isOkdCompliant)
		{
			return false;
		}
		if (isOsiCompliant != licence.isOsiCompliant)
		{
			return false;
		}
		if (!title.equals(licence.title))
		{
			return false;
		}
		if (!family.equals(licence.family))
		{
			return false;
		}
		if (!id.equals(licence.id))
		{
			return false;
		}
		if (!maintainer.equals(licence.maintainer))
		{
			return false;
		}
		if (status != licence.status)
		{
			return false;
		}
		if (!url.equals(licence.url))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({"ConditionalExpression", "FeatureEnvy"})
	@Override
	public int hashCode()
	{
		int result = status.hashCode();
		result = 31 * result + maintainer.hashCode();
		result = 31 * result + title.hashCode();
		result = 31 * result + family.hashCode();
		result = 31 * result + url.hashCode();
		result = 31 * result + (domainData ? 1 : 0);
		result = 31 * result + (isGeneric ? 1 : 0);
		result = 31 * result + (isOkdCompliant ? 1 : 0);
		result = 31 * result + (isOsiCompliant ? 1 : 0);
		result = 31 * result + (domainContent ? 1 : 0);
		result = 31 * result + (domainSoftware ? 1 : 0);
		result = 31 * result + id.hashCode();
		return result;
	}
}
