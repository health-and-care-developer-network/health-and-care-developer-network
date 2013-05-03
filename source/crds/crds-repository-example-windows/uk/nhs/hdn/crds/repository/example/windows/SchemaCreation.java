package uk.nhs.hdn.crds.repository.example.windows;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.sql.ConnectionProvider;
import uk.nhs.hdn.common.sql.SchemaCreator;

import static uk.nhs.hdn.common.ResourceLoader.utf8TextResourceContents;

public final class SchemaCreation
{
	private SchemaCreation()
	{
	}

	public static void createOrReplaceSchema(@NotNull final ConnectionProvider connectionProvider)
	{
		new SchemaCreator(connectionProvider).createSchema
		(
			utf8TextResourceContents("RepositoryExampleWindows.database.sql", SchemaCreation.class)
		);
	}
}
