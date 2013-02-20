package uk.nhs.hdn.ckan.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.PackageId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;

import java.util.Arrays;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hdn.common.serialisers.AbstractSerialiser.writeNullableProperty;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

public final class Revision extends AbstractToString implements Serialisable, MapSerialisable
{
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String idField = "id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String timestampField = "timestamp";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String messageField = "message";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String authorField = "author";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String approvedTimestampField = "approved_timestamp";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String packagesField = "packages";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String groupsField = "groups";

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		idField,
		timestampField,
		messageField,
		authorField,
		approvedTimestampField,
		packagesField,
		groupsField,
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf(idField, 0),
		leaf(timestampField, 1),
		leaf(messageField, 2),
		leaf(authorField, 3),
		leaf(approvedTimestampField, 4),
		leaf(packagesField, 5),
		leaf(groupsField, 6)
	);

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForRevisions(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForRevisions()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@NotNull public final RevisionId id;
	@NotNull public final MicrosecondTimestamp timestamp;
	@Nullable @NonNls public final String message;
	@Nullable @NonNls public final String author;
	@Nullable public final MicrosecondTimestamp approvedTimestamp;
	@NotNull public final PackageId[] packages;
	@NotNull public final GroupId[] groups;

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	public Revision(@NotNull final RevisionId id, @NotNull final MicrosecondTimestamp timestamp, @Nullable @NonNls final String message, @Nullable @NonNls final String author, @Nullable final MicrosecondTimestamp approvedTimestamp, @NotNull final PackageId[] packages, @NotNull final GroupId[] groups)
	{
		this.id = id;
		this.timestamp = timestamp;
		this.message = message;
		this.author = author;
		this.approvedTimestamp = approvedTimestamp;
		this.packages = copyOf(packages);
		this.groups = copyOf(groups);
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
			mapSerialiser.writeProperty(idField, id);
			mapSerialiser.writeProperty(timestampField, timestamp);
			writeNullableProperty(mapSerialiser, messageField, message);
			writeNullableProperty(mapSerialiser, authorField, author);
			writeNullableProperty(mapSerialiser, approvedTimestampField, approvedTimestamp);
			mapSerialiser.writeProperty(packagesField, packages);
			mapSerialiser.writeProperty(groupsField, groups);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings({"OverlyComplexMethod", "ConditionalExpression"})
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

		final Revision revision = (Revision) obj;

		if (approvedTimestamp != null ? !approvedTimestamp.equals(revision.approvedTimestamp) : revision.approvedTimestamp != null)
		{
			return false;
		}
		if (author != null ? !author.equals(revision.author) : revision.author != null)
		{
			return false;
		}
		if (!Arrays.equals(groups, revision.groups))
		{
			return false;
		}
		if (!id.equals(revision.id))
		{
			return false;
		}
		if (message != null ? !message.equals(revision.message) : revision.message != null)
		{
			return false;
		}
		if (!Arrays.equals(packages, revision.packages))
		{
			return false;
		}
		if (!timestamp.equals(revision.timestamp))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings("ConditionalExpression")
	@Override
	public int hashCode()
	{
		int result = id.hashCode();
		result = 31 * result + timestamp.hashCode();
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (author != null ? author.hashCode() : 0);
		result = 31 * result + (approvedTimestamp != null ? approvedTimestamp.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(packages);
		result = 31 * result + Arrays.hashCode(groups);
		return result;
	}
}
