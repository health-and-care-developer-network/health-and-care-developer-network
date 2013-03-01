package uk.nhs.hdn.ckan.client.query;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.Api;
import uk.nhs.hdn.ckan.api.ApiMethod;
import uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetKey;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.naming.Description;
import uk.nhs.hdn.common.serialisers.Serialisable;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;

import static uk.nhs.hdn.ckan.domain.Revision.tsvSerialiserForRevisions;
import static uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp.microsecondTimestamp;
import static uk.nhs.hdn.ckan.domain.ids.DatasetId.tsvSerialiserForDatasetIds;
import static uk.nhs.hdn.ckan.domain.ids.RevisionId.tsvSerialiserForRevisionIds;
import static uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName.tsvSerialiserForDatasetNames;

public enum QueryAction implements Description
{
	revisions_since_timestamp("Microsecond Timestamp, eg 2013-01-28T20:06:30.061645")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return microsecondTimestamp(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.revisions((MicrosecondTimestamp) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForRevisionIds();
		}
	},
	revisions_since_id("Revision UUID")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return RevisionId.valueOf(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.revisions((RevisionId) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForRevisionIds();
		}
	},
	revisions_by_name("Revision Name")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return new DatasetName(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.datasetRevisions((DatasetKey) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForRevisions();
		}
	},
	revisions_by_id("Revision UUID")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return RevisionId.valueOf(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.datasetRevisions((DatasetKey) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForRevisions();
		}
	},
	dataset_ids_by_tag("Tag")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return new TagName(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.datasetIdsWithTag((TagName) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForDatasetIds();
		}
	},
	dataset_names_by_tag("Tag")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return new TagName(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.datasetNamesWithTag((TagName) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForDatasetNames();
		}
	},
	;

	@NonNls @NotNull private final String description;

	QueryAction(@NotNull @NonNls final String description)
	{
		this.description = description;
	}

	@NotNull
	@NonNls
	@Override
	public String description()
	{
		return description;
	}

	@NotNull
	public abstract Object parseKey(@NotNull final String value);

	@NotNull
	public abstract ApiMethod<? extends Serialisable[]> apiMethod(@NotNull final Api api, @NotNull final Object key);

	@NotNull
	public abstract SeparatedValueSerialiser tsvSerialiser();

	public void execute(@NotNull final Api api, @NotNull final String key) throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final Object parsedKey = parseKey(key);
		final Serialisable[] result = apiMethod(api, parsedKey).get();
		tsvSerialiser().printValuesOnStandardOut(result);
	}
}
