/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.fileWatching;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface FileReloader
{
	void reload() throws FailedToReloadException;
}
