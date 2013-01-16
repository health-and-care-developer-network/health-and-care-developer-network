/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, FIELD, METHOD, LOCAL_VARIABLE})
public @interface ProtocolAndVersion
{
	@NonNls
	@ProtocolAndVersion
	@NotNull
	String HTTP10 = "HTTP/1.0";

	@NonNls
	@ProtocolAndVersion
	@NotNull
	String HTTP11 = "HTTP/1.1";
}
