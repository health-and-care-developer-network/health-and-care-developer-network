package uk.nhs.hdn.common.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.util.UUID.fromString;

public final class ResultSetHelper
{
	private ResultSetHelper()
	{
	}

	@NotNull
	public static UUID getUuid(@NotNull final ResultSet resultSet, final int columnIndex) throws SQLException
	{
		return fromString(resultSet.getNString(columnIndex));
	}
}
