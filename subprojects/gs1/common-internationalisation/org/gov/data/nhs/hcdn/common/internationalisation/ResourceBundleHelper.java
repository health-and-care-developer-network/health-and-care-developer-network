package org.gov.data.nhs.hcdn.common.internationalisation;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class ResourceBundleHelper
{
	private ResourceBundleHelper()
	{
	}

	@NotNull
	public static PropertyResourceBundle propertyResourceBundle(@NotNull final Class<?> clazzNearestToBundle, @NonNls @NotNull final String propertiesBundleNameIncludingExtension)
	{
		@SuppressWarnings("IOResourceOpenedButNotSafelyClosed") @Nullable final InputStream resourceAsStream = clazzNearestToBundle.getResourceAsStream(propertiesBundleNameIncludingExtension);
		guardMissingResource(propertiesBundleNameIncludingExtension, resourceAsStream);
		try
		{
			boolean succeeded = false;
			final PropertyResourceBundle propertyResourceBundle;
			try
			{
				propertyResourceBundle = new PropertyResourceBundle(resourceAsStream);
				succeeded = true;
			}
			finally
			{

				try
				{
					resourceAsStream.close();
				}
				catch (IOException e)
				{
					if (succeeded)
					{
						throw e;
					}
				}
			}
			return propertyResourceBundle;
		}
		catch (IOException e)
		{
			@NonNls final String format = "The resource bundle %1$s did not load successfully";
			throw new IllegalStateException(format(ENGLISH, format, propertiesBundleNameIncludingExtension), e);
		}
	}

	private static void guardMissingResource(final String propertiesBundleNameIncludingExtension, final InputStream resourceAsStream)
	{
		if (resourceAsStream == null)
		{
			@NonNls final String format = "The resource bundle %1$s is missing";
			throw new IllegalStateException(format(ENGLISH, format, propertiesBundleNameIncludingExtension));
		}
	}
}
