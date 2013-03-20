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
import org.openhealthtools.ihe.bridge.type.AddressType;
import uk.nhs.hdn.common.postCodes.PostCode;
import uk.nhs.hdn.ihe.xds.builders.abstractions.AbstractAddressBuilder;

import static org.openhealthtools.ihe.bridge.type.AddressType.EMPTY_ADDRESS;

public final class AddressBuilder extends AbstractAddressBuilder
{
	@SuppressWarnings("ConstantNamingConvention") @NotNull public static final AddressBuilder address = new AddressBuilder();

	@NotNull
	public static Type address(@NotNull @NonNls final String type)
	{
		return address.type(type);
	}

	private AddressBuilder()
	{
	}

	@NotNull
	@Override
	public AddressType build()
	{
		return EMPTY_ADDRESS;
	}

	@NotNull
	public Type type(@NotNull @NonNls final String type)
	{
		return new Type(type);
	}

	@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass"})
	public final class Type extends AbstractAddressBuilder
	{
		@NotNull @NonNls private final String type;

		private Type(@NotNull @NonNls final String type)
		{
			this.type = type;
		}

		@NotNull
		@Override
		public AddressType build()
		{
			final AddressType done = new AddressType();
			done.setAddressType(type);
			return done;
		}

		@NotNull
		public Country country(@NotNull @NonNls final String country)
		{
			return new Country(country);
		}

		@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass", "InnerClassTooDeeplyNested"})
		public final class Country extends AbstractAddressBuilder
		{
			@NotNull @NonNls private final String country;

			private Country(@NotNull @NonNls final String country)
			{
				this.country = country;
			}

			@NotNull
			@Override
			public AddressType build()
			{
				final AddressType done = Type.this.build();
				done.setCountry(country);
				done.setStateOrProvince(country);
				return done;
			}

			@NotNull
			public PostOrZipCode postOrZipCode(@NotNull final PostCode postCode)
			{
				return new PostOrZipCode(postCode);
			}

			@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass", "InnerClassTooDeeplyNested"})
			public final class PostOrZipCode extends AbstractAddressBuilder
			{
				@NotNull private final PostCode postCode;

				private PostOrZipCode(@NotNull final PostCode postCode)
				{
					this.postCode = postCode;
				}

				@NotNull
				@Override
				public AddressType build()
				{
					final AddressType done = Country.this.build();
					done.setZipOrPostalCode(postCode.normalised());
					return done;
				}

				@NotNull
				public AddressType country(@NotNull @NonNls final String houseNumberAndStreetLine)
				{
					final AddressType done = build();
					done.setStreetAddress(houseNumberAndStreetLine);
					return done;
				}
			}
		}
	}
}
