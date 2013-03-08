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

package uk.nhs.hdn.ckan.api;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.search.SearchCriteria;
import uk.nhs.hdn.ckan.api.search.StringSearchCriterion;
import uk.nhs.hdn.ckan.api.search.searchDelegates.AbstractSearchDelegate;
import uk.nhs.hdn.ckan.api.search.searchDelegates.SearchDelegate;
import uk.nhs.hdn.ckan.domain.*;
import uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.ResourceId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.strings.Hash;
import uk.nhs.hdn.ckan.domain.uniqueNames.*;
import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.json.JsonGenericGetApi;
import uk.nhs.hdn.common.parsers.json.JsonSchema;

import static uk.nhs.hdn.ckan.api.search.UnsignedLongSearchCriterion.*;
import static uk.nhs.hdn.ckan.schema.DatasetIdSearchObjectJsonSchema.DatasetIdSearchSchemaInstance;
import static uk.nhs.hdn.ckan.schema.DatasetIdsArrayJsonSchema.DatasetIdsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.DatasetJsonSchema.DatasetSchemaInstance;
import static uk.nhs.hdn.ckan.schema.DatasetNameSearchObjectJsonSchema.DatasetNameSearchSchemaInstance;
import static uk.nhs.hdn.ckan.schema.DatasetNamesArrayJsonSchema.DatasetNamesSchemaInstance;
import static uk.nhs.hdn.ckan.schema.GroupIdsArrayJsonSchema.GroupIdsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.GroupJsonSchema.GroupSchemaInstance;
import static uk.nhs.hdn.ckan.schema.GroupNamesArrayJsonSchema.GroupNamesSchemaInstance;
import static uk.nhs.hdn.ckan.schema.LicencesArrayJsonSchema.LicencesSchemaInstance;
import static uk.nhs.hdn.ckan.schema.ResourceIdSearchObjectJsonSchema.ResourceIdSearchSchemaInstance;
import static uk.nhs.hdn.ckan.schema.RevisionIdsArrayJsonSchema.RevisionIdsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.RevisionJsonSchema.RevisionSchemaInstance;
import static uk.nhs.hdn.ckan.schema.RevisionsArrayJsonSchema.RevisionsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.TagCountsArrayJsonSchema.TagCountsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.TagsArrayJsonSchema.TagsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.arrayCreators.DatasetIdArrayCreator.DatasetIdArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.DatasetNameArrayCreator.DatasetNameArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.ResourceIdArrayCreator.ResourceIdArray;
import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.tuples.Pair.pair;

@SuppressWarnings({"ClassNamePrefixedWithPackageName", "ClassWithTooManyMethods"})
public final class ConcreteCkanApi implements CkanApi
{
	@SuppressWarnings("ConstantNamingConvention") private static final String version1 = "1";
	@SuppressWarnings("ConstantNamingConvention") private static final String version2 = "2";
	@SuppressWarnings("ConstantNamingConvention") private static final String api = "api";
	@SuppressWarnings("ConstantNamingConvention") private static final String rest = "rest";
	@SuppressWarnings("ConstantNamingConvention") private static final String revision = "revision";
	@SuppressWarnings("ConstantNamingConvention") private static final String resource = "resource";
	@SuppressWarnings("ConstantNamingConvention") private static final String dataset = "dataset";
	@SuppressWarnings("ConstantNamingConvention") private static final String group = "group";
	@SuppressWarnings("ConstantNamingConvention") private static final String tag = "tag";
	@SuppressWarnings("ConstantNamingConvention") private static final String search = "search";

	@NotNull public static final CkanApi DataGovUk = new ConcreteCkanApi(new JsonGenericGetApi(false, "data.gov.uk", ""));

	@NotNull private final JsonGenericGetApi jsonGenericGetApi;

	public ConcreteCkanApi(@NotNull final JsonGenericGetApi jsonGenericGetApi)
	{
		this.jsonGenericGetApi = jsonGenericGetApi;
	}

	@Override
	@NotNull
	public ApiMethod<DatasetName[]> allDatasetNames()
	{
		return jsonGenericGetApi.newApiMethod(DatasetNamesSchemaInstance, api, version1, rest, dataset);
	}

	@Override
	@NotNull
	public ApiMethod<DatasetId[]> allDatasetIds()
	{
		return jsonGenericGetApi.newApiMethod(DatasetIdsSchemaInstance, api, version2, rest, dataset);
	}

	@Override
	@NotNull
	public ApiMethod<Dataset> dataset(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DatasetKey datasetKey)
	{
		return jsonGenericGetApi.newApiMethod(DatasetSchemaInstance, api, version2, rest, dataset, datasetKey.value());
	}

	@Override
	@NotNull
	public ApiMethod<Revision[]> datasetRevisions(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DatasetKey datasetKey)
	{
		return jsonGenericGetApi.newApiMethod(RevisionsSchemaInstance, api, version2, rest, dataset, datasetKey.value(), "revisions");
	}

	@Override
	@NotNull
	public ApiMethod<DatasetName[]> datasetRelationshipsByDatasetName(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DatasetName datasetName, @NotNull final RelationshipType relationshipType)
	{
		return jsonGenericGetApi.newApiMethod(DatasetNamesSchemaInstance, api, version1, rest, dataset, datasetName.value(), relationshipType.name());
	}

	@Override
	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipsByDatasetId(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DatasetKey datasetKey, @NotNull final RelationshipType relationshipType)
	{
		return jsonGenericGetApi.newApiMethod(DatasetIdsSchemaInstance, api, version2, rest, dataset, datasetKey.value(), relationshipType.name());
	}

	@Override
	@NotNull
	public ApiMethod<GroupName[]> allGroupNames()
	{
		return jsonGenericGetApi.newApiMethod(GroupNamesSchemaInstance, api, version1, rest, group);
	}

	@Override
	@NotNull
	public ApiMethod<GroupId[]> allGroupIds()
	{
		return jsonGenericGetApi.newApiMethod(GroupIdsSchemaInstance, api, version2, rest, group);
	}

	@Override
	@NotNull
	public ApiMethod<Group> group(@SuppressWarnings("TypeMayBeWeakened") @NotNull final GroupKey groupKey)
	{
		return jsonGenericGetApi.newApiMethod(GroupSchemaInstance, api, version2, rest, group, groupKey.value());
	}

	@Override
	@NotNull
	public ApiMethod<TagName[]> allTags()
	{
		return jsonGenericGetApi.newApiMethod(TagsSchemaInstance, api, version2, rest, tag);
	}

	@Override
	@NotNull
	public ApiMethod<DatasetName[]> datasetNamesWithTag(@NotNull final TagName tagName)
	{
		return jsonGenericGetApi.newApiMethod(DatasetNamesSchemaInstance, api, version1, rest, tag, tagName.value());
	}

	@Override
	@NotNull
	public ApiMethod<DatasetId[]> datasetIdsWithTag(@NotNull final TagName tagName)
	{
		return jsonGenericGetApi.newApiMethod(DatasetIdsSchemaInstance, api, version2, rest, tag, tagName.value());
	}

	@Override
	@NotNull
	public ApiMethod<Licence[]> allLicences()
	{
		return jsonGenericGetApi.newApiMethod(LicencesSchemaInstance, api, version2, rest, "licenses");
	}

	@Override
	@NotNull
	public ApiMethod<Revision> revision(@SuppressWarnings("TypeMayBeWeakened") @NotNull final RevisionId revisionId)
	{
		return jsonGenericGetApi.newApiMethod(RevisionSchemaInstance, api, version2, rest, revision, revisionId.value());
	}

	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<SearchResult<DatasetName>> datasetNames(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SearchCriteria<Dataset> searchCriteria, final long offset, final long limit)
	{
		return newApiMethod(DatasetNameSearchSchemaInstance, of(api, version1, search, dataset), datasetsWithOffsetAndLimit(searchCriteria, offset, limit));
	}

	@Override
	@NotNull
	public SearchDelegate<DatasetName, Dataset> datasetNameSearchDelegate()
	{
		return new AbstractSearchDelegate<DatasetName, Dataset>(DatasetNameArray)
		{
			@NotNull
			@Override
			public ApiMethod<SearchResult<DatasetName>> search(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SearchCriteria<Dataset> searchCriteria, final long offset, final long limit)
			{
				return datasetNames(searchCriteria, offset, limit);
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<SearchResult<DatasetId>> datasetIds(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SearchCriteria<Dataset> searchCriteria, final long offset, final long limit)
	{
		return newApiMethod(DatasetIdSearchSchemaInstance, of(api, version2, search, dataset), datasetsWithOffsetAndLimit(searchCriteria, offset, limit));
	}

	@Override
	@NotNull
	public SearchDelegate<DatasetId, Dataset> datasetIdSearchDelegate()
	{
		return new AbstractSearchDelegate<DatasetId, Dataset>(DatasetIdArray)
		{
			@NotNull
			@Override
			public ApiMethod<SearchResult<DatasetId>> search(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SearchCriteria<Dataset> searchCriteria, final long offset, final long limit)
			{
				return datasetIds(searchCriteria, offset, limit);
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<SearchResult<ResourceId>> resourceIds(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Hash hash, final long offset, final long limit)
	{
		return newApiMethod(ResourceIdSearchSchemaInstance, of(api, version2, search, resource), resourcesWithOffsetAndLimit(new StringSearchCriterion<>(Resource.class, "hash", hash.value()), offset, limit));
	}

	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<SearchResult<ResourceId>> resourceIds(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SearchCriteria<Resource> searchCriteria, final long offset, final long limit)
	{
		return newApiMethod(ResourceIdSearchSchemaInstance, of(api, version2, search, resource), resourcesWithOffsetAndLimit(searchCriteria, offset, limit));
	}

	@Override
	@NotNull
	public SearchDelegate<ResourceId, Resource> resourceSearchDelegate()
	{
		return new AbstractSearchDelegate<ResourceId, Resource>(ResourceIdArray)
		{
			@NotNull
			@Override
			public ApiMethod<SearchResult<ResourceId>> search(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SearchCriteria<Resource> searchCriteria, final long offset, final long limit)
			{
				return resourceIds(searchCriteria, offset, limit);
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<RevisionId[]> revisions(@SuppressWarnings("TypeMayBeWeakened") @NotNull final RevisionId since)
	{
		return jsonGenericGetApi.newApiMethod(RevisionIdsSchemaInstance, of(api, version2, search, revision), pair("since_id", since.value()));
	}

	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<RevisionId[]> revisions(@NotNull final MicrosecondTimestamp since)
	{
		return jsonGenericGetApi.newApiMethod(RevisionIdsSchemaInstance, of(api, version2, search, revision), pair("since_time", since.toString()));
	}

	@Override
	@NotNull
	public ApiMethod<TagCount[]> tagCounts()
	{
		return jsonGenericGetApi.newApiMethod(TagCountsSchemaInstance, api, version2, "tag_counts");
	}

	@NotNull
	private <V> ApiMethod<V> newApiMethod(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String[] urlPieces, @NotNull final SearchCriteria<?> searchCriteria)
	{
		return jsonGenericGetApi.newApiMethod(jsonSchema, urlPieces, searchCriteria.toUnencodedQueryStringParameters());
	}

	private static SearchCriteria<Dataset> datasetsWithOffsetAndLimit(final SearchCriteria<Dataset> searchCriteria, final long offset, final long limit)
	{
		return searchCriteria.and(datasetOffsetSearchCriterion(offset)).and(datasetLimitSearchCriterion(limit));
	}

	private static SearchCriteria<Resource> resourcesWithOffsetAndLimit(final SearchCriteria<Resource> searchCriteria, final long offset, final long limit)
	{
		return searchCriteria.and(resourceOffsetSearchCriterion(offset)).and(resourceLimitSearchCriterion(limit));
	}
}
