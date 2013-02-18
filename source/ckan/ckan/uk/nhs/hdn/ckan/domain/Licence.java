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
import uk.nhs.hdn.ckan.domain.strings.*;
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

	@NotNull
	private final Status status;
	@NotNull
	private final Maintainer maintainer;
	@NotNull
	private final Family family;
	@NotNull
	private final Title title;
	@NotNull
	private final Url url;
	private final boolean isGeneric; // not always present so needs default
	private final boolean isOkdCompliant;
	private final boolean isOsiCompliant;
	private final boolean domainData;
	private final boolean domainContent;
	private final boolean domainSoftware;
	@NotNull
	private final LicenceId id;

	@SuppressWarnings("ConstructorWithTooManyParameters")
	public Licence(@NotNull final Status status, @NotNull final Maintainer maintainer, @NotNull final Family family, @NotNull final Title title, @NotNull final Url url, final boolean isGeneric, final boolean isOkdCompliant, final boolean isOsiCompliant, final boolean domainData, final boolean domainContent, final boolean domainSoftware, @NotNull final LicenceId id)
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

/*
[{
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "License Not Specified",
	"url": "",
	"domain_data": false,
	"is_generic": true,
	"is_okd_compliant": false,
	"is_osi_compliant": false,
	"domain_content": false,
	"domain_software": false,
	"id": "notspecified"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Open Data Commons Public Domain Dedication and Licence (PDDL)",
	"domain_data": true,
	"url": "http://www.opendefinition.org/licenses/odc-pddl",
	"domain_content": false,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "odc-pddl"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Open Data Commons Open Database License (ODbL)",
	"domain_data": true,
	"url": "http://www.opendefinition.org/licenses/odc-odbl",
	"domain_content": false,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "odc-odbl"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Open Data Commons Attribution License",
	"domain_data": true,
	"url": "http://www.opendefinition.org/licenses/odc-by",
	"domain_content": false,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "odc-by"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Creative Commons CCZero",
	"domain_data": true,
	"url": "http://www.opendefinition.org/licenses/cc-zero",
	"domain_content": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "cc-zero"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Creative Commons Attribution",
	"domain_data": false,
	"url": "http://www.opendefinition.org/licenses/cc-by",
	"domain_content": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "cc-by"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Creative Commons Attribution Share-Alike",
	"domain_data": false,
	"url": "http://www.opendefinition.org/licenses/cc-by-sa",
	"domain_content": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "cc-by-sa"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "GNU Free Documentation License",
	"domain_data": false,
	"url": "http://www.opendefinition.org/licenses/gfdl",
	"domain_content": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "gfdl"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Other (Open)",
	"url": "",
	"domain_data": false,
	"is_generic": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_content": true,
	"domain_software": false,
	"id": "other-open"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Other (Public Domain)",
	"url": "",
	"domain_data": false,
	"is_generic": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_content": true,
	"domain_software": false,
	"id": "other-pd"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Other (Attribution)",
	"url": "",
	"domain_data": false,
	"is_generic": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_content": true,
	"domain_software": false,
	"id": "other-at"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "UK Open Government Licence (OGL)",
	"domain_data": false,
	"url": "http://reference.data.gov.uk/id/open-government-licence",
	"domain_content": true,
	"is_okd_compliant": true,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "uk-ogl"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Creative Commons Non-Commercial (Any)",
	"domain_data": false,
	"url": "http://creativecommons.org/licenses/by-nc/2.0/",
	"domain_content": false,
	"is_okd_compliant": false,
	"is_osi_compliant": false,
	"domain_software": false,
	"id": "cc-nc"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Other (Non-Commercial)",
	"url": "",
	"domain_data": false,
	"is_generic": true,
	"is_okd_compliant": false,
	"is_osi_compliant": false,
	"domain_content": false,
	"domain_software": false,
	"id": "other-nc"
}, {
	"status": "active",
	"maintainer": "",
	"family": "",
	"title": "Other (Not Open)",
	"url": "",
	"domain_data": false,
	"is_generic": true,
	"is_okd_compliant": false,
	"is_osi_compliant": false,
	"domain_content": false,
	"domain_software": false,
	"id": "other-closed"
}]



*/
