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

package uk.nhs.hdn.crds.store.server.rest.metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.caching.caching.SourceOfValuesToCache;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.crds.store.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.store.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor;
import uk.nhs.hdn.crds.store.recordStore.RecordStore;

import static java.util.Locale.UK;

public final class MetadataRecordSubResourceSourceOfValuesToCache implements SourceOfValuesToCache<Identifier, MetadataRecordSubResource>
{
	private final RecordStore<Identifier, AbstractMetadataRecord<?>> metadataRecordStore;
	@NotNull private final String xmlRootElementName;
	@NotNull private final SeparatedValueSerialiser tsvSerialiser;
	@NotNull private final SeparatedValueSerialiser csvSerialiser;

	public MetadataRecordSubResourceSourceOfValuesToCache(@NotNull final RecordStore<Identifier, AbstractMetadataRecord<?>> metadataRecordStore, @NotNull final IdentifierConstructor identifierConstructor)
	{
		this.metadataRecordStore = metadataRecordStore;
		xmlRootElementName = identifierConstructor.name().toLowerCase(UK);
		tsvSerialiser = identifierConstructor.tsvSerialiser();
		csvSerialiser = identifierConstructor.csvSerialiser(true);
	}

	@Nullable
	@Override
	public MetadataRecordSubResource get(@NotNull final Identifier key)
	{
		@Nullable final AbstractMetadataRecord<?> metadataRecord = metadataRecordStore.get(key);
		if (metadataRecord == null)
		{
			return null;
		}
		return new MetadataRecordSubResource(metadataRecord.lastModified, xmlRootElementName, tsvSerialiser, csvSerialiser, metadataRecord);
	}
}
