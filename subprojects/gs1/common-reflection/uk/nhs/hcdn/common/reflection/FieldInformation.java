package uk.nhs.hcdn.common.reflection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import java.lang.reflect.Field;

public final class FieldInformation extends AbstractToString
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static FieldInformation fieldInformation(@NotNull final Field field)
	{
		return new FieldInformation(field);
	}

	@NotNull
	private final Field field;

	public FieldInformation(@NotNull final Field field)
	{
		this.field = field;
	}

	public boolean hasAnnotation(@NotNull final Class<ExcludeFromToString> annotationClass)
	{
		return !doesNotHaveAnnotation(annotationClass);
	}

	public boolean doesNotHaveAnnotation(@NotNull final Class<ExcludeFromToString> annotationClass)
	{
		return field.getAnnotation(annotationClass) == null;
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}

		final FieldInformation that = (FieldInformation) obj;

		if (!field.equals(that.field))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return field.hashCode();
	}
}
