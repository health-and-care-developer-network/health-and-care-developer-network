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

package uk.nhs.hdn.crds.registry.domain.metadata.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.FieldParser;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor;

import java.util.UUID;

import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyUUIDFieldParser2.NonEmptyUUIDFieldParserInstance;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Provider;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Repository;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Stuff;

public final class IdentifierFieldParser<I extends Identifier> implements FieldParser<I>
{
	@NotNull public static final IdentifierFieldParser<ProviderIdentifier> ProviderIdentifierFieldParserInstance = new IdentifierFieldParser<>(Provider);
	@NotNull public static final IdentifierFieldParser<RepositoryIdentifier> RepositoryIdentifierFieldParserInstance = new IdentifierFieldParser<>(Repository);
	@NotNull public static final IdentifierFieldParser<RepositoryIdentifier> StuffIdentifierFieldParserInstance = new IdentifierFieldParser<>(Stuff);

	private final IdentifierConstructor identifierConstructor;

	IdentifierFieldParser(@NotNull final IdentifierConstructor identifierConstructor)
	{
		this.identifierConstructor = identifierConstructor;
	}

	@Override
	public boolean skipIfEmpty()
	{
		return NonEmptyUUIDFieldParserInstance.skipIfEmpty();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public I parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		final UUID uuid = NonEmptyUUIDFieldParserInstance.parse(fieldIndex, fieldValue);
		return (I) identifierConstructor.construct(uuid);
	}
}
