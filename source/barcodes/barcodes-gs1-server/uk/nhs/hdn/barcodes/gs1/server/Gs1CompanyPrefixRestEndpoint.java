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

package uk.nhs.hdn.barcodes.gs1.server;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.barcodes.gs1.server.parsing.Gs1CompanyPrefixResourceStateSnapshotUser;
import uk.nhs.hdn.barcodes.gs1.server.parsing.ParsingFileReloader;
import uk.nhs.hdn.barcodes.gs1.server.parsing.TuplesParserFactory;
import uk.nhs.hdn.common.fileWatching.FailedToReloadException;
import uk.nhs.hdn.common.fileWatching.FileWatcher;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.AbstractRegisterableMethodRoutingRestEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.GetMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.HeadMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hdn.common.parsers.ParserFactory;
import uk.nhs.hdn.common.parsers.sources.FileSource;

import java.io.File;
import java.util.Map;

import static java.lang.Thread.MIN_PRIORITY;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;

public final class Gs1CompanyPrefixRestEndpoint extends AbstractRegisterableMethodRoutingRestEndpoint<Gs1CompanyPrefixResourceStateSnapshot> implements Gs1CompanyPrefixResourceStateSnapshotUser
{
	// volatile - it may be changed during a request
	@SuppressWarnings("InstanceVariableMayNotBeInitialized") // Actually, it is - by callback
	@NotNull
	private volatile Gs1CompanyPrefixResourceStateSnapshot gs1CompanyPrefixResourceStateSnapshot;

	public Gs1CompanyPrefixRestEndpoint(@NotNull final File containingFolder, @NonNls @NotNull final String fileName)
	{
		super("/gs1/organisation/", NoAuthentication);

		final FileSource fileSource = new FileSource(Utf8, new File(containingFolder, fileName).toPath().toAbsolutePath());
		@SuppressWarnings("ThisEscapedInObjectConstruction") final ParserFactory parserFactory = new TuplesParserFactory(this);
		final ParsingFileReloader fileReloader = new ParsingFileReloader(parserFactory, fileSource);
		try
		{
			fileReloader.reload();
		}
		catch (FailedToReloadException e)
		{
			throw new IllegalStateException("Could not load tuples", e);
		}
		final FileWatcher fileWatcher = new FileWatcher(containingFolder, fileName, fileReloader);
		final Thread thread = new Thread(fileWatcher, getClass().getSimpleName() + "FileWatcher");
		thread.setDaemon(true);
		thread.setPriority(MIN_PRIORITY);
		thread.start();
	}

	@Override
	public void use(@NotNull final Gs1CompanyPrefixResourceStateSnapshot gs1CompanyPrefixResourceStateSnapshot)
	{
		this.gs1CompanyPrefixResourceStateSnapshot = gs1CompanyPrefixResourceStateSnapshot;
	}

	@Override
	protected void registerMethodEndpointsExcludingOptions(@NotNull final Map<String, MethodEndpoint<Gs1CompanyPrefixResourceStateSnapshot>> methodEndpointsRegister)
	{
		HeadMethodEndpoint.<Gs1CompanyPrefixResourceStateSnapshot>headMethodEndpoint().register(methodEndpointsRegister);
		GetMethodEndpoint.<Gs1CompanyPrefixResourceStateSnapshot>getMethodEndpoint().register(methodEndpointsRegister);
	}

	@NotNull
	@Override
	protected Gs1CompanyPrefixResourceStateSnapshot snapshotOfStateThatIsInvariantForRequest()
	{
		return gs1CompanyPrefixResourceStateSnapshot;
	}
}
