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
import org.openhealthtools.ihe.bridge.type.PatientNameType;
import uk.nhs.hdn.ihe.xds.builders.abstractions.AbstractPatientNameBuilder;

import static org.openhealthtools.ihe.bridge.type.PatientNameType.EMPTY_NAME;

public final class PatientNameBuilder extends AbstractPatientNameBuilder
{
	@SuppressWarnings("ConstantNamingConvention") @NotNull public static final PatientNameBuilder patientName = new PatientNameBuilder();

	@NotNull
	public static FamilyName patientName(@NotNull @NonNls final String familyName)
	{
		return patientName.familyName(familyName);
	}

	private PatientNameBuilder()
	{
	}

	@NotNull
	@Override
	public PatientNameType build()
	{
		return EMPTY_NAME;
	}

	@NotNull
	public FamilyName familyName(@NotNull @NonNls final String familyName)
	{
		return new FamilyName(familyName);
	}

	@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass"})
	public final class FamilyName extends AbstractPatientNameBuilder
	{
		@NotNull @NonNls private final String familyName;

		private FamilyName(@NotNull @NonNls final String familyName)
		{
			this.familyName = familyName;
		}

		@NotNull
		@Override
		public PatientNameType build()
		{
			final PatientNameType done = new PatientNameType();
			done.setFamilyName(familyName);
			return done;
		}

		@NotNull
		public GivenName givenName(@NotNull @NonNls final String firstName)
		{
			return new GivenName(firstName);
		}

		@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass", "InnerClassTooDeeplyNested"})
		public final class GivenName extends AbstractPatientNameBuilder
		{
			@NotNull @NonNls private final String givenName;

			private GivenName(@NotNull @NonNls final String givenName)
			{
				this.givenName = givenName;
			}

			@NotNull
			@Override
			public PatientNameType build()
			{
				final PatientNameType done = FamilyName.this.build();
				done.setGivenName(givenName);
				return done;
			}

			@NotNull
			public OtherNames otherNames(@NotNull @NonNls final String otherNames)
			{
				return new OtherNames(otherNames);
			}

			@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass", "InnerClassTooDeeplyNested"})
			public final class OtherNames extends AbstractPatientNameBuilder
			{
				@NotNull @NonNls private final String otherNames;

				private OtherNames(@NotNull @NonNls final String otherNames)
				{
					this.otherNames = otherNames;
				}

				@NotNull
				@Override
				public PatientNameType build()
				{
					final PatientNameType done = GivenName.this.build();
					done.setOtherName(otherNames);
					return done;
				}

				@NotNull
				public PatientNameType prefix(@NotNull @NonNls final String prefix)
				{
					final PatientNameType done = build();
					done.setPrefix(prefix);
					return done;
				}
			}
		}
	}
}
