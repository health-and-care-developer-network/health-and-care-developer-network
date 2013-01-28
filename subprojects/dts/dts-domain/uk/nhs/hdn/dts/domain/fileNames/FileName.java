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

package uk.nhs.hdn.dts.domain.fileNames;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.dts.domain.TransactionSequenceIdentifier;

import java.io.File;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.dts.domain.TransactionSequenceIdentifier.Width;
import static uk.nhs.hdn.dts.domain.TransactionSequenceIdentifier.fromPaddedTransactionSequenceIdentifer;
import static uk.nhs.hdn.dts.domain.fileNames.FileExtension.ctl;
import static uk.nhs.hdn.dts.domain.fileNames.FileExtension.dat;

public final class FileName
{
	private static final int NoIndex = -1;
	private static final int Period = (int) '.';
	private static final int MinimumSiteIdentifierIncludingApp = 1;
	private static final int MinimumSize = Width + MinimumSiteIdentifierIncludingApp;

	@NotNull
	public static FileName parseFileName(@NotNull final String fileNameIncludingExtension)
	{
		final int lastIndex = fileNameIncludingExtension.lastIndexOf(Period);
		if (lastIndex == NoIndex)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Filename %1$s has no file extension", fileNameIncludingExtension));
		}

		final String extension = fileNameIncludingExtension.substring(lastIndex + 1);
		@Nullable final FileExtension fileExtension = FileExtension.fileExtension(extension);
		if (fileExtension == null)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Filename %1$s has an unrecognised file extension %2$s", fileNameIncludingExtension, extension));
		}

		@SuppressWarnings("UnnecessaryLocalVariable") final int lengthOfFilenameExcludingExtension = lastIndex;
		if (lengthOfFilenameExcludingExtension < MinimumSize)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Filename %1$s is too small", fileNameIncludingExtension));
		}

		final String siteIdentifierIncludingApp = fileNameIncludingExtension.substring(0, lengthOfFilenameExcludingExtension - Width);

		final String paddedSequenceIdentifier = fileNameIncludingExtension.substring(lengthOfFilenameExcludingExtension - Width, lastIndex);
		final TransactionSequenceIdentifier sequenceIdentifier = fromPaddedTransactionSequenceIdentifer(paddedSequenceIdentifier);

		return new FileName(siteIdentifierIncludingApp, sequenceIdentifier, fileExtension);
	}

	public static FileName controlFileName(@NotNull final String siteIdentifierIncludingApp, @NotNull final TransactionSequenceIdentifier sequenceIdentifier)
	{
		return new FileName(siteIdentifierIncludingApp, sequenceIdentifier, ctl);
	}

	public static FileName controlFileName(@NotNull final String siteIdentifier, @NotNull final String app, @NotNull final TransactionSequenceIdentifier sequenceIdentifier)
	{
		return new FileName(siteIdentifier, app, sequenceIdentifier, ctl);
	}

	public static FileName dataFileName(@NotNull final String siteIdentifierIncludingApp, @NotNull final TransactionSequenceIdentifier sequenceIdentifier)
	{
		return new FileName(siteIdentifierIncludingApp, sequenceIdentifier, dat);
	}

	public static FileName dataFileName(@NotNull final String siteIdentifier, @NotNull final String app, @NotNull final TransactionSequenceIdentifier sequenceIdentifier)
	{
		return new FileName(siteIdentifier, app, sequenceIdentifier, dat);
	}

	@NonNls
	@NotNull
	private final String siteIdentifier;

	@NonNls
	@NotNull
	private final String app;

	@NotNull
	private final TransactionSequenceIdentifier sequenceIdentifier;

	@NotNull
	private final FileExtension fileExtension;

	private final boolean siteIdentifierIncludesIndistinguishableApp;

	public FileName(@NotNull final String siteIdentifierIncludingApp, @NotNull final TransactionSequenceIdentifier sequenceIdentifier, @NotNull final FileExtension fileExtension)
	{
		if (siteIdentifierIncludingApp.isEmpty())
		{
			throw new IllegalStateException("siteIdentifierIncludingApp can not be empty");
		}
		siteIdentifier = siteIdentifierIncludingApp;
		app = "";
		this.sequenceIdentifier = sequenceIdentifier;
		this.fileExtension = fileExtension;
		siteIdentifierIncludesIndistinguishableApp = true;
	}

	public FileName(@NotNull final String siteIdentifier, @NotNull final String app, @NotNull final TransactionSequenceIdentifier sequenceIdentifier, @NotNull final FileExtension fileExtension)
	{
		if (siteIdentifier.isEmpty())
		{
			throw new IllegalStateException("siteIdentifier can not be empty");
		}
		if (app.isEmpty())
		{
			throw new IllegalStateException("app can not be empty");
		}
		this.siteIdentifier = siteIdentifier;
		this.app = app;
		this.sequenceIdentifier = sequenceIdentifier;
		this.fileExtension = fileExtension;
		siteIdentifierIncludesIndistinguishableApp = false;
	}

	@NotNull
	public Path onPath(@NotNull final Path path)
	{
		return path.resolve(toFileNameString());
	}

	@NotNull
	public File onPath(@NotNull final File parentFolder)
	{
		return new File(parentFolder, toFileNameString());
	}

	@NotNull
	public String toFileNameString()
	{
		return format(ENGLISH, "%1$s%2$s%3$s.%4$s", siteIdentifier, app, sequenceIdentifier.toPaddedValue(), fileExtension.name());
	}

	@NotNull
	public FileName toControlFileName()
	{
		return toFileName(ctl);
	}

	@NotNull
	public FileName toDataFileName()
	{
		return toFileName(dat);
	}

	@NotNull
	private FileName toFileName(@NotNull final FileExtension fileExtension)
	{
		if (this.fileExtension == fileExtension)
		{
			return this;
		}
		if (siteIdentifierIncludesIndistinguishableApp)
		{
			return new FileName(siteIdentifier, sequenceIdentifier, fileExtension);
		}
		else
		{
			return new FileName(siteIdentifier, app, sequenceIdentifier, fileExtension);
		}
	}

	@Override
	@NotNull
	public String toString()
	{
		if (siteIdentifierIncludesIndistinguishableApp)
		{
			return format(ENGLISH, "%1$s(%2$s, %3$s, %4$s)", getClass().getSimpleName(), siteIdentifier, sequenceIdentifier, fileExtension);
		}
		return format(ENGLISH, "%1$s(%2$s, %3$s, %4$s, %5$s)", getClass().getSimpleName(), siteIdentifier, app, sequenceIdentifier, fileExtension);
	}

	@NotNull
	@NonNls
	public String siteIdentifierIncludingApp()
	{
		if (siteIdentifierIncludesIndistinguishableApp)
		{
			return siteIdentifier;
		}
		return siteIdentifier + app;
	}

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

		final FileName that = (FileName) obj;

		if (fileExtension != that.fileExtension)
		{
			return false;
		}
		if (!sequenceIdentifier.equals(that.sequenceIdentifier))
		{
			return false;
		}
		if (!siteIdentifierIncludingApp().equals(that.siteIdentifierIncludingApp()))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = siteIdentifierIncludingApp().hashCode();
		result = 31 * result + sequenceIdentifier.hashCode();
		result = 31 * result + fileExtension.hashCode();
		return result;
	}

	public boolean hasFileExtension(@NotNull final FileExtension fileExtension)
	{
		return this.fileExtension == fileExtension;
	}
}
