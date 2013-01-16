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
public @interface Method
{
	@NonNls
	@Method
	@NotNull
	String OPTIONS = "OPTIONS";

	@NonNls
	@Method
	@NotNull
	String TRACE = "TRACE";

	@NonNls
	@Method
	@NotNull
	String HEAD = "HEAD";

	@NonNls
	@Method
	@NotNull
	String GET = "GET";

	@NonNls
	@Method
	@NotNull
	String POST = "POST";

	@NonNls
	@Method
	@NotNull
	String PUT = "PUT";

	@NonNls
	@Method
	@NotNull
	String DELETE = "DELETE";

	@NonNls
	@Method
	@NotNull
	String CONNECT = "CONNECT";

	@NonNls
	@Method
	@NotNull
	String PATCH = "PATCH";
}
