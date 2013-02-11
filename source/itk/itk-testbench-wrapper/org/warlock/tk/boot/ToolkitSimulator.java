package org.warlock.tk.boot;/*
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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ToolkitSimulator
{
	@SuppressWarnings("UnusedDeclaration")
	@NonNls
	@NotNull
	String getConfigurationName();

	@SuppressWarnings("UnusedDeclaration")
	@NonNls
	@NotNull
	String getVersion();

	@SuppressWarnings("UnusedDeclaration")
	@NonNls
	@NotNull
	String getOrganisationName();

	@Deprecated
	@SuppressWarnings("ProhibitedExceptionDeclared")
	void initValidate() throws Exception;

	@Deprecated
	@SuppressWarnings("ProhibitedExceptionDeclared")
	void initSpineValidate() throws Exception;

	@Deprecated
	@SuppressWarnings("ProhibitedExceptionDeclared")
	void initAutoTest() throws Exception;

	@Deprecated
	@SuppressWarnings("ProhibitedExceptionDeclared")
	void initTransmit() throws Exception;

	@Deprecated
	@SuppressWarnings("ProhibitedExceptionDeclared")
	void boot() throws Exception;

	@SuppressWarnings("UnusedDeclaration")
	@NonNls
	@NotNull
	Set<String> getServiceNames();

	@SuppressWarnings("UnusedDeclaration")
	@Deprecated
	@NotNull
	ToolkitService getService(@NotNull final String sname);
}
