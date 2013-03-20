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
import org.openhealthtools.ihe.bridge.type.AuthorType;
import org.openhealthtools.ihe.bridge.type.PatientInfoType;
import uk.nhs.hdn.ihe.xds.builders.abstractions.AbstractAuthorBuilder;
import uk.nhs.hdn.ihe.xds.builders.abstractions.Builder;

public final class AuthorBuilder extends AbstractAuthorBuilder
{
	@SuppressWarnings("ConstantNamingConvention") @NotNull public static final AuthorBuilder author = new AuthorBuilder();

	@NotNull
	public static Name author(@NotNull final Builder<PatientInfoType> authorPerson)
	{
		return author(authorPerson.build());
	}

	@NotNull
	public static Name author(@NotNull final PatientInfoType authorPerson)
	{
		return author.name(authorPerson);
	}

	private AuthorBuilder()
	{
	}

	@NotNull
	@Override
	public AuthorType build()
	{
		return new AuthorType();
	}

	@NotNull
	public Name name(@NotNull final PatientInfoType authorPerson)
	{
		return new Name(authorPerson);
	}

	@NotNull
	public Name name(@NotNull final PatientInfoBuilder authorPerson)
	{
		return new Name(authorPerson.build());
	}

	@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass"})
	public final class Name extends AbstractAuthorBuilder
	{
		@NotNull private final PatientInfoType authorPerson;

		private Name(@NotNull final PatientInfoType authorPerson)
		{
			this.authorPerson = authorPerson;
		}

		@NotNull
		@Override
		public AuthorType build()
		{
			final AuthorType done = AuthorBuilder.this.build();
			done.setAuthorPerson(authorPerson);
			return done;
		}

		@NotNull
		public Specialities specialities(@NotNull @NonNls final String... specialities)
		{
			return new Specialities(specialities);
		}

		@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass", "InnerClassTooDeeplyNested"})
		public final class Specialities extends AbstractAuthorBuilder
		{
			@NotNull private final String[] specialities;

			private Specialities(@NotNull @NonNls final String... specialities)
			{
				this.specialities = specialities;
			}

			@Override
			@NotNull
			public AuthorType build()
			{
				final AuthorType done = Name.this.build();
				done.setAuthorSpecialty(specialities);
				return done;
			}

			@NotNull
			public Institutions institutions(@NotNull final AuthorInstitutionType... institutions)
			{
				return new Institutions(institutions);
			}

			@SuppressWarnings("FinalMethodInFinalClass")
			@SafeVarargs
			@NotNull
			public final Institutions institutions(@NotNull final Builder<AuthorInstitutionType>... institutionBuilders)
			{
				final int length = institutionBuilders.length;
				final AuthorInstitutionType[] institutions = new AuthorInstitutionType[length];
				for (int index = 0; index < length; index++)
				{
					final Builder<AuthorInstitutionType> institutionBuilder = institutionBuilders[index];
					institutions[index] = institutionBuilder.build();
				}
				return institutions(institutions);
			}

			public final class Institutions extends AbstractAuthorBuilder
			{
				@NotNull private final AuthorInstitutionType[] institutions;

				private Institutions(@NotNull final AuthorInstitutionType... institutions)
				{
					this.institutions = institutions;
				}

				@NotNull
				@Override
				public AuthorType build()
				{
					final AuthorType done = Specialities.this.build();
					done.setAuthorInstitution(institutions);
					return done;
				}

				@NotNull
				public AuthorType roles(@NotNull @NonNls final String... roles)
				{
					final AuthorType done = build();
					done.setAuthorRole(roles);
					return done;
				}
			}
		}
	}
}
