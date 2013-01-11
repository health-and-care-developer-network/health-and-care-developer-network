package uk.nhs.hcdn.barcodes.gs1.server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

public interface RestEndpoint extends HttpHandler
{
	void register(@NotNull HttpServer httpServer);
}
