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

package uk.nhs.hdn.ihe.xds.builders;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.openhealthtools.ihe.bridge.type.AuthorInstitutionType;
import org.openhealthtools.ihe.bridge.type.PatientIdType;
import uk.nhs.hdn.ihe.xds.builders.abstractions.AbstractAuthorInstitutionBuilder;

public final class AuthorInstitutionBuilder extends AbstractAuthorInstitutionBuilder
{
	@SuppressWarnings("ConstantNamingConvention") @NotNull public static final AuthorInstitutionBuilder authorInstitution = new AuthorInstitutionBuilder();

	@NotNull
	public static Organisation authorInstitution(@NotNull @NonNls final String organisation)
	{
		return authorInstitution.organisation(organisation);
	}

	private AuthorInstitutionBuilder()
	{
	}

	@NotNull
	@Override
	public AuthorInstitutionType build()
	{
		return new AuthorInstitutionType();
	}

	@NotNull
	public Organisation organisation(@NotNull @NonNls final String organisation)
	{
		return new Organisation(organisation);
	}

	@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass"})
	public final class Organisation extends AbstractAuthorInstitutionBuilder
	{
		@NotNull @NonNls private final String organisation;

		private Organisation(@NotNull @NonNls final String organisation)
		{
			this.organisation = organisation;
		}

		@NotNull
		@Override
		public AuthorInstitutionType build()
		{
			final AuthorInstitutionType done = AuthorInstitutionBuilder.this.build();
			done.setOrganizationName(organisation);
			return done;
		}

		@NotNull
		public AuthorInstitutionType id(@NotNull final PatientIdType id)
		{
			final AuthorInstitutionType done = build();
			done.setOrganizationId(id);
			return done;
		}
	}
}
