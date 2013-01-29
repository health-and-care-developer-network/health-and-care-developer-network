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

package uk.nhs.hdn.common.http;

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
