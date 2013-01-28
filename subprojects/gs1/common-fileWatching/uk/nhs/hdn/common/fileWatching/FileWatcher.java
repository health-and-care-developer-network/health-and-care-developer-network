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

package uk.nhs.hdn.common.fileWatching;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class FileWatcher extends AbstractToString implements Runnable
{
	private static final long MillisecondsToWait = 1000L;

	@NotNull
	private final AtomicBoolean run;

	@NotNull
	private final WatchService watcher;

	@NotNull
	private final Path folderPath;

	@NotNull
	private final FileReloader fileReloader;

	@NotNull
	private final Path watchedPath;

	public FileWatcher(@NotNull final File folder, @NotNull final String fileName, @NotNull final FileReloader fileReloader)
	{
		this.fileReloader = fileReloader;
		if (!folder.exists() || !folder.isDirectory())
		{
			throw new IllegalStateException(format(ENGLISH, "Folder %1$s does not exist", folder));
		}
		watchedPath = new File(folder, fileName).toPath().toAbsolutePath();
		run = new AtomicBoolean(true);
		try
		{
			watcher = getDefault().newWatchService();
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Could not out a watch service", e);
		}
		folderPath = folder.toPath();
		try
		{
			folderPath.register(watcher, ENTRY_MODIFY, ENTRY_CREATE);
		}
		catch (IOException e)
		{
			throw new IllegalStateException(format(ENGLISH, "Could not watch path %1$s", folderPath), e);
		}
	}

	@Override
	public void run()
	{
		while(run.get())
		{
			@Nullable final WatchKey watchKey;
			try
			{
				watchKey = watcher.poll(MillisecondsToWait, MILLISECONDS);
			}
			catch (InterruptedException ignored)
			{
				run.set(false);
				return;
			}
			if (watchKey == null)
			{
				continue;
			}
			final List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
			for (final WatchEvent<?> watchEvent : watchEvents)
			{
				if (watchEvent.kind().equals(OVERFLOW))
				{
					continue;
				}
				@SuppressWarnings("unchecked") final WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
				final Path context = pathWatchEvent.context();
				final Path resolve = folderPath.resolve(context).toAbsolutePath();
				if (resolve.equals(watchedPath))
				{
					try
					{
						fileReloader.reload();
					}
					catch (FailedToReloadException e)
					{
						e.printStackTrace();
					}
				}
			}
			if (!watchKey.reset())
			{
				run.set(false);
				throw new IllegalStateException("Watched folder path deleted");
			}
		}
	}
}
