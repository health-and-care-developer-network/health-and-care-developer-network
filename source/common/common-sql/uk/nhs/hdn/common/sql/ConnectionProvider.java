package uk.nhs.hdn.common.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public interface ConnectionProvider
{
	@NotNull
	Connection connection();
}
