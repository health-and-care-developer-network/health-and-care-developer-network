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

package uk.nhs.hcdn.common.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, FIELD, METHOD, LOCAL_VARIABLE})
public @interface XmlNamespaceUri
{
	@XmlNamespaceUri
	@NotNull
	@NonNls
	String XmlNamespace = "http://www.w3.org/XML/1998/namespace"; // does not need to be declared

	@XmlNamespaceUri
	@NotNull
	@NonNls
	String XmlnsNamespace = "http://www.w3.org/2000/xmlns/"; // does not need to be declared

	@XmlNamespaceUri
	@NotNull
	@NonNls
	String XmlSchemaNamesapce = "http://www.w3.org/2001/XMLSchema";

	@XmlNamespaceUri
	@NotNull
	@NonNls
	String XmlSchemaInstanceNamespace = "http://www.w3.org/2001/XMLSchema-instance";
}
