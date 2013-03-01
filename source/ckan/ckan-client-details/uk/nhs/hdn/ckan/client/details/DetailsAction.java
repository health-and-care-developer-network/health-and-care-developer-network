package uk.nhs.hdn.ckan.client.details;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.Api;
import uk.nhs.hdn.ckan.api.ApiMethod;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetKey;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupKey;
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupName;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.naming.Description;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.Serialisable;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;

import static uk.nhs.hdn.ckan.domain.Dataset.tsvSerialiserForDatasets;
import static uk.nhs.hdn.ckan.domain.Group.tsvSerialiserForGroups;
import static uk.nhs.hdn.ckan.domain.ids.RevisionId.tsvSerialiserForRevisionIds;
import static uk.nhs.hdn.ckan.schema.arrayCreators.DatasetArrayCreator.DatasetArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.GroupArrayCreator.GroupArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.RevisionIdArrayCreator.RevisionIdArray;

public enum DetailsAction implements Description
{
	dataset_by_name("Dataset Name")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return new DatasetName(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends MapSerialisable> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.dataset((DatasetKey) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForDatasets();
		}

		@NotNull
		@Override
		public ArrayCreator<? extends Serialisable> arrayCreator()
		{
			return DatasetArray;
		}
	},
	dataset_by_id("Dataset UUID")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return DatasetId.valueOf(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends MapSerialisable> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.dataset((DatasetKey) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForDatasets();
		}

		@NotNull
		@Override
		public ArrayCreator<? extends Serialisable> arrayCreator()
		{
			return DatasetArray;
		}
	},
	group_by_name("Group Name")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return new GroupName(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends MapSerialisable> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.group((GroupKey) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForGroups();
		}

		@NotNull
		@Override
		public ArrayCreator<? extends Serialisable> arrayCreator()
		{
			return GroupArray;
		}
	},
	group_by_id("Group UUID")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return GroupId.valueOf(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends MapSerialisable> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.group((GroupKey) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForGroups();
		}

		@NotNull
		@Override
		public ArrayCreator<? extends Serialisable> arrayCreator()
		{
			return GroupArray;
		}
	},
	revision_by_id("Revision UUID")
	{
		@NotNull
		@Override
		public Object parseKey(@NotNull final String value)
		{
			return RevisionId.valueOf(value);
		}

		@NotNull
		@Override
		public ApiMethod<? extends MapSerialisable> apiMethod(@NotNull final Api api, @NotNull final Object key)
		{
			return api.revision((RevisionId) key);
		}

		@NotNull
		@Override
		public SeparatedValueSerialiser tsvSerialiser()
		{
			return tsvSerialiserForRevisionIds();
		}

		@NotNull
		@Override
		public ArrayCreator<? extends Serialisable> arrayCreator()
		{
			return RevisionIdArray;
		}
	},
	;

	@NotNull private final String description;

	DetailsAction(@NotNull @NonNls final String description)
	{
		this.description = description;
	}

	@NotNull
	public abstract Object parseKey(@NotNull final String value);

	@NotNull
	public abstract ApiMethod<? extends MapSerialisable> apiMethod(@NotNull final Api api, @NotNull final Object key);

	@NotNull
	public abstract SeparatedValueSerialiser tsvSerialiser();

	@NotNull
	public abstract ArrayCreator<? extends Serialisable> arrayCreator();

	public void execute(@NotNull final Api api, @NotNull final String key) throws CouldNotConnectHttpException, CorruptResponseException
	{
		final Object parsedKey = parseKey(key);
		final ApiMethod<? extends MapSerialisable> apiMethod = apiMethod(api, parsedKey);
		final SeparatedValueSerialiser separatedValueSerialiser = tsvSerialiser();
		final MapSerialisable result;
		try
		{
			result = apiMethod.get();
		}
		catch (UnacceptableResponseException e)
		{
			if (e.isNotFound())
			{
				separatedValueSerialiser.printValuesOnStandardOut(arrayCreator().newInstance1(0));
				return;
			}
			throw new IllegalStateException(e);
		}
		final Serialisable[] values = arrayCreator().newInstance1(1);
		values[0] = (Serialisable) result;
		separatedValueSerialiser.printValuesOnStandardOut(values);
	}

	@NonNls
	@NotNull
	@Override
	public String description()
	{
		return description;
	}
}
