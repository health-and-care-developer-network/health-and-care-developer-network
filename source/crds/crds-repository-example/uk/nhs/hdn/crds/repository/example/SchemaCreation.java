package uk.nhs.hdn.crds.repository.example;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.sql.ConnectionProvider;
import uk.nhs.hdn.common.sql.SchemaCreator;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;

import static uk.nhs.hdn.common.ResourceLoader.utf8TextResourceContents;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.provider;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.repository;

public final class SchemaCreation
{
	@NotNull public static final ProviderIdentifier OurProviderIdentifier = (ProviderIdentifier) provider.construct("2dbf298f-eed9-474d-bf8b-d70f68b83417");
	@NotNull public static final RepositoryIdentifier OurRepositoryIdentifier = (RepositoryIdentifier) repository.construct("3b942abe-3ec0-4830-bdb9-d1197dadf520");

	private SchemaCreation()
	{
	}

	public static void createOrReplaceSchema(@NotNull final ConnectionProvider connectionProvider)
	{
		new SchemaCreator(connectionProvider).createSchema
		(
			utf8TextResourceContents("RepositoryExample.database.sql", SchemaCreation.class),
			utf8TextResourceContents("RepositoryExample.patients.sql", SchemaCreation.class)
		);
	}
}
