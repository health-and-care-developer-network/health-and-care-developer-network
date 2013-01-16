/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, FIELD, METHOD, LOCAL_VARIABLE})
public @interface ResponseCode
{
	@ResponseCode
	int OkResponseCode = 200;

	@ResponseCode
	int NoContentResponseCode = 204;

	@ResponseCode
	int NotModifiedResponseCode = 304;

	@ResponseCode
	int BadRequestResponseCode = 400;

	@ResponseCode
	int NotFoundResponseCode = 404;

	@ResponseCode
	int MethodNotAllowedResponseCode = 405;

	@ResponseCode
	int PreconditionFailedResponseCode = 412;

	@ResponseCode
	int ExpectationFailedResponseCode = 417;

	@ResponseCode
	int HttpVersionNotSupportedResponseCode = 505;
}
