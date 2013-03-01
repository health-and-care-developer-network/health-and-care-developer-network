package uk.nhs.hdn.ckan.client.list;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.Api;
import uk.nhs.hdn.ckan.api.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.serialisers.Serialisable;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;

import static uk.nhs.hdn.ckan.domain.Licence.tsvSerialiserForLicences;
import static uk.nhs.hdn.ckan.domain.TagCount.tsvSerialiserForTagCounts;
import static uk.nhs.hdn.ckan.domain.ids.DatasetId.tsvSerialiserForDatasetIds;
import static uk.nhs.hdn.ckan.domain.ids.GroupId.tsvSerialiserForGroupIds;
import static uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName.tsvSerialiserForDatasetNames;
import static uk.nhs.hdn.ckan.domain.uniqueNames.GroupName.tsvSerialiserForGroupNames;
import static uk.nhs.hdn.ckan.domain.uniqueNames.TagName.tsvSerialiserForTags;

public enum ListAction
{
	dataset_names
	{
		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api)
		{
			return api.allDatasetNames();
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForDatasetNames();
		}
	},
	dataset_ids
	{
		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api)
		{
			return api.allDatasetIds();
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForDatasetIds();
		}
	},
	group_names
	{
		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api)
		{
			return api.allGroupNames();
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForGroupNames();
		}
	},
	group_ids
	{
		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api)
		{
			return api.allGroupIds();
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForGroupIds();
		}
	},
	tags
	{
		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api)
		{
			return api.allTags();
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForTags();
		}
	},
	licences
	{
		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api)
		{
			return api.allLicences();
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForLicences();
		}
	},
	tag_counts
	{
		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api)
		{
			return api.tagCounts();
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForTagCounts();
		}
	},
	;

	@NotNull
	public abstract ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api);

	@NotNull
	public abstract SeparatedValueSerialiser tsvSerialiser();

	public void execute(@NotNull final Api api) throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final Serialisable[] result = apiMethod(api).get();
		final SeparatedValueSerialiser separatedValueSerialiser = tsvSerialiser();
		separatedValueSerialiser.printValuesOnStandardOut(result);
	}
}
