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

package uk.nhs.hdn.ckan.api;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.naming.Description;

public enum RelationshipType implements Description
{
	depends_on,
	dependency_on,
	derives_from,
	has_derivation,
	child_of,
	parent_of,
	links_to,
	linked_from,
	;

	@NotNull
	@Override
	public String description()
	{
		return "Dataset Name or UUID (UUIDs do not work when returning results as Dataset Names)";
	}

}
