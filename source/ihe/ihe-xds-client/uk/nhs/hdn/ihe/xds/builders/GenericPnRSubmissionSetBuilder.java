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
import org.openhealthtools.ihe.bridge.type.GenericPnRSubmissionSet;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.number.NhsNumber;

import static uk.nhs.hdn.ihe.xds.builders.abstractions.AbstractBuilder.newUniqueId;

public final class GenericPnRSubmissionSetBuilder extends AbstractToString
{
	@SuppressWarnings("ConstantNamingConvention") @NotNull public static final GenericPnRSubmissionSetBuilder genericPnRSubmissionSet = new GenericPnRSubmissionSetBuilder();

	@NotNull
	public static ContentTypeCode genericPnRSubmissionSet(@NotNull final CodedMetadataType contentTypeCode)
	{
		return genericPnRSubmissionSet.contentTypeCode(contentTypeCode);
	}

	private GenericPnRSubmissionSetBuilder()
	{
	}

	@NotNull
	public ContentTypeCode contentTypeCode(@NotNull final CodedMetadataType contentTypeCode)
	{
		return new ContentTypeCode(contentTypeCode);
	}

	@SuppressWarnings({"NonStaticInnerClassInSecureContext", "PublicInnerClass"})
	public final class ContentTypeCode extends AbstractToString
	{
		@NotNull private final CodedMetadataType contentTypeCode;

		private ContentTypeCode(@NotNull final CodedMetadataType contentTypeCode)
		{
			this.contentTypeCode = contentTypeCode;
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

			final ContentTypeCode that = (ContentTypeCode) obj;

			if (!contentTypeCode.equals(that.contentTypeCode))
			{
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			return contentTypeCode.hashCode();
		}

		@NotNull
		public GenericPnRSubmissionSet patientId(@NotNull final NhsNumber nhsNumber)
		{
			return patientId(nhsNumber, newUniqueId());
		}

		@NotNull
		public GenericPnRSubmissionSet patientId(@NotNull final NhsNumber nhsNumber, @NotNull @NonNls final String sourceId)
		{
			final GenericPnRSubmissionSet done = new GenericPnRSubmissionSet();
			done.setContentTypeCode(contentTypeCode);
			done.setSourceId(sourceId);
			done.setPatientId(PatientIdBuilder.patientId(nhsNumber));
			done.setUniqueId(newUniqueId());
			return done;
		}
	}
}
