package uk.nhs.hdn.common.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionUser
{
	void useConnection(@NotNull final Connection connection) throws SQLException;
}
