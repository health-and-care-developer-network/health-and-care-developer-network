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
import org.jetbrains.annotations.Nullable;
import org.openhealthtools.ihe.bridge.type.CodedMetadataType;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

public final class CodedMetadataBuilder extends AbstractToString
{
	@SuppressWarnings("ConstantNamingConvention") @NotNull public static final CodedMetadataBuilder codedMetadata = new CodedMetadataBuilder();

	@NotNull
	public static CodedMetadataType codedMetadata(@NotNull @NonNls final String codeName, @NotNull @NonNls final String schemeName, @NotNull @NonNls final String displayName)
	{
		return new CodedMetadataType(codeName, schemeName, displayName);
	}

	@NotNull
	public static Scheme codedMetadata(@NotNull @NonNls final String scheme)
	{
		return codedMetadata.scheme(scheme);
	}

	private CodedMetadataBuilder()
	{
	}

	@NotNull
	public Scheme scheme(@NotNull @NonNls final String scheme)
	{
		return new Scheme(scheme);
	}

	@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass"})
	public final class Scheme extends AbstractToString
	{
		@NotNull @NonNls private final String scheme;

		private Scheme(@NotNull @NonNls final String scheme)
		{
			this.scheme = scheme;
		}

		@Override
		public boolean equals(@Nullable final Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null || getClass() != obj.getClass())
			{
				return false;
			}

			final Scheme scheme1 = (Scheme) obj;

			if (!scheme.equals(scheme1.scheme))
			{
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			return scheme.hashCode();
		}

		@NotNull
		public Code code(@NotNull @NonNls final String code)
		{
			return new Code(code);
		}

		@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass", "InnerClassTooDeeplyNested"})
		public final class Code extends AbstractToString
		{
			@NotNull @NonNls private final String code;

			private Code(@NotNull @NonNls final String code)
			{
				this.code = code;
			}

			@Override
			public boolean equals(@Nullable final Object obj)
			{
				if (this == obj)
				{
					return true;
				}
				if (obj == null || getClass() != obj.getClass())
				{
					return false;
				}

				final Code code1 = (Code) obj;

				if (!code.equals(code1.code))
				{
					return false;
				}

				return true;
			}

			@Override
			public int hashCode()
			{
				return code.hashCode();
			}

			@NotNull
			public CodedMetadataType displayName(@NotNull @NonNls final String displayName)
			{
				return new CodedMetadataType(code, scheme, displayName);
			}
		}
	}
}
