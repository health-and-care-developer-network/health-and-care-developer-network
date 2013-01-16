/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.fileWatching;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

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
			throw new IllegalStateException("Could not create a watch service", e);
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
			final WatchKey watchKey;
			try
			{
				watchKey = watcher.poll(MillisecondsToWait, MILLISECONDS);
			}
			catch (InterruptedException ignored)
			{
				run.set(false);
				return;
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
