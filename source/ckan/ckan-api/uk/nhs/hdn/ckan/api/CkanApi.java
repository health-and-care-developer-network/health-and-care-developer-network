package uk.nhs.hdn.ckan.api;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.search.SearchCriteria;
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

@SuppressWarnings("ClassWithTooManyMethods")
public interface CkanApi
{
	@NotNull
	ApiMethod<DatasetName[]> allDatasetNames();

	@NotNull
	ApiMethod<DatasetId[]> allDatasetIds();

	@NotNull
	ApiMethod<GroupName[]> allGroupNames();

	@NotNull
	ApiMethod<GroupId[]> allGroupIds();

	@NotNull
	ApiMethod<TagName[]> allTags();

	@NotNull
	ApiMethod<Licence[]> allLicences();

	@NotNull
	ApiMethod<TagCount[]> tagCounts();

	@NotNull
	ApiMethod<Dataset> dataset(@SuppressWarnings("TypeMayBeWeakened") @NotNull DatasetKey datasetKey);

	@NotNull
	ApiMethod<Revision[]> datasetRevisions(@SuppressWarnings("TypeMayBeWeakened") @NotNull DatasetKey datasetKey);

	@NotNull
	ApiMethod<Group> group(@SuppressWarnings("TypeMayBeWeakened") @NotNull GroupKey groupKey);

	@NotNull
	ApiMethod<DatasetName[]> datasetNamesWithTag(@NotNull TagName tagName);

	@NotNull
	ApiMethod<DatasetId[]> datasetIdsWithTag(@NotNull TagName tagName);

	@NotNull
	ApiMethod<Revision> revision(@SuppressWarnings("TypeMayBeWeakened") @NotNull RevisionId revisionId);

	@SuppressWarnings("unchecked")
	@NotNull
	ApiMethod<RevisionId[]> revisions(@SuppressWarnings("TypeMayBeWeakened") @NotNull RevisionId since);

	@SuppressWarnings("unchecked")
	@NotNull
	ApiMethod<RevisionId[]> revisions(@NotNull MicrosecondTimestamp since);

	@NotNull
	ApiMethod<DatasetName[]> datasetRelationshipsByDatasetName(@SuppressWarnings("TypeMayBeWeakened") @NotNull DatasetName datasetName, @NotNull RelationshipType relationshipType);

	@NotNull
	ApiMethod<DatasetId[]> datasetRelationshipsByDatasetId(@SuppressWarnings("TypeMayBeWeakened") @NotNull DatasetKey datasetKey, @NotNull RelationshipType relationshipType);

	@SuppressWarnings("unchecked")
	@NotNull
	ApiMethod<SearchResult<DatasetName>> datasetNames(@SuppressWarnings("TypeMayBeWeakened") @NotNull SearchCriteria<Dataset> searchCriteria, long offset, long limit);

	@SuppressWarnings("unchecked")
	@NotNull
	ApiMethod<SearchResult<DatasetId>> datasetIds(@SuppressWarnings("TypeMayBeWeakened") @NotNull SearchCriteria<Dataset> searchCriteria, long offset, long limit);

	@SuppressWarnings("unchecked")
	@NotNull
	ApiMethod<SearchResult<ResourceId>> resourceIds(@SuppressWarnings("TypeMayBeWeakened") @NotNull Hash hash, long offset, long limit);

	@SuppressWarnings("unchecked")
	@NotNull
	ApiMethod<SearchResult<ResourceId>> resourceIds(@SuppressWarnings("TypeMayBeWeakened") @NotNull SearchCriteria<Resource> searchCriteria, long offset, long limit);

	@NotNull
	SearchDelegate<DatasetId, Dataset> datasetIdSearchDelegate();

	@NotNull
	SearchDelegate<DatasetName, Dataset> datasetNameSearchDelegate();

	@NotNull
	SearchDelegate<ResourceId, Resource> resourceSearchDelegate();
}
