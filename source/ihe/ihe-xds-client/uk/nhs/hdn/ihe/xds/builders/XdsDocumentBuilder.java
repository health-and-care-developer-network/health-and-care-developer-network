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
import org.openhealthtools.ihe.bridge.type.AuthorType;
import org.openhealthtools.ihe.bridge.type.CodedMetadataType;
import org.openhealthtools.ihe.bridge.type.XDSDocType;
import sun.misc.BASE64Encoder;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.ihe.xds.builders.abstractions.AbstractBuilder;
import uk.nhs.hdn.number.NhsNumber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Arrays.copyOf;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.MessageDigestHelper.SHA512;
import static uk.nhs.hdn.ihe.xds.builders.PatientIdBuilder.patientId;

public final class XdsDocumentBuilder extends AbstractBuilder<XDSDocType>
{
	// Yeah, I know.
	@SuppressWarnings("UseOfSunClasses") private static final BASE64Encoder Base64Encoder = new BASE64Encoder();

	@NotNull
	public static XdsDocumentBuilder xdsDocument(@NotNull final byte[] binaryDocumentContent, @NotNull @NonNls final String languageCode, @NotNull @NonNls final String mimeType, @NotNull final EncodingType encodingType, @NotNull final CodedMetadataType formatCode, @NotNull final CodedMetadataType typeCode, @MillisecondsSince1970 final long creationTime, @NotNull @NonNls final String title, @NotNull final NhsNumber patientNhsNumber, @NotNull final AuthorType... authors)
	{
		return new XdsDocumentBuilder(binaryDocumentContent, languageCode, mimeType, encodingType, formatCode, typeCode, creationTime, title, patientNhsNumber, authors);
	}

	@NotNull private final byte[] binaryDocumentContent;
	@NotNull @NonNls private final String languageCode;
	@NotNull @NonNls private final String mimeType;
	@NotNull private final EncodingType encodingType;
	@NotNull private final CodedMetadataType formatCode;
	@NotNull private final CodedMetadataType typeCode;
	@MillisecondsSince1970 private final long creationTime;
	@NotNull @NonNls private final String title;
	@NotNull private final NhsNumber patientNhsNumber;
	@NotNull private final AuthorType[] authors;
	@Nullable private UUID parentDocumentUniqueId;
	@Nullable private ParentDocumentRelationship parentDocumentRelationship;
	@Nullable @NonNls private String comments;

	public XdsDocumentBuilder(@NotNull final byte[] binaryDocumentContent, @NotNull @NonNls final String languageCode, @NotNull @NonNls final String mimeType, @NotNull final EncodingType encodingType, @NotNull final CodedMetadataType formatCode, @NotNull final CodedMetadataType typeCode, @MillisecondsSince1970 final long creationTime, @NotNull @NonNls final String title, @NotNull final NhsNumber patientNhsNumber, @NotNull final AuthorType... authors)
	{
		if (encodingType.isLegacy)
		{
			throw new IllegalArgumentException(format(ENGLISH, "legacy encodings such as %1$s should not be used", encodingType.actualName()));
		}

		final int authorsLength = authors.length;
		if (authorsLength == 0)
		{
			throw new IllegalArgumentException("authors can not be empty");
		}

		this.binaryDocumentContent = copyOf(binaryDocumentContent, binaryDocumentContent.length);
		this.languageCode = languageCode;
		this.mimeType = mimeType;
		this.encodingType = encodingType;
		this.formatCode = formatCode;
		this.typeCode = typeCode;
		this.creationTime = creationTime;
		this.title = title;
		this.patientNhsNumber = patientNhsNumber;
		this.authors = copyOf(authors, authorsLength);

		parentDocumentUniqueId = null;
		parentDocumentRelationship = null;
		comments = null;
	}

	@NotNull
	public XdsDocumentBuilder parentRelationship(@NotNull final UUID parentDocumentUniqueId, @NotNull final ParentDocumentRelationship parentDocumentRelationship)
	{
		if (this.parentDocumentUniqueId != null)
		{
			throw new IllegalStateException("parentRelatiopnship already specified");
		}
		this.parentDocumentUniqueId = parentDocumentUniqueId;
		this.parentDocumentRelationship = parentDocumentRelationship;
		return this;
	}

	@NotNull
	public XdsDocumentBuilder comments(@NotNull @NonNls final String comments)
	{
		if (this.comments != null)
		{
			throw new IllegalStateException("comments already specified");
		}
		this.comments = comments;
		return this;
	}

	@NotNull
	@Override
	public XDSDocType build()
	{
		final XDSDocType xdsDocument = new XDSDocType();

		xdsDocument.setBase64EncodedDocument(Base64Encoder.encode(binaryDocumentContent));
		xdsDocument.setHash(Base64Encoder.encode(SHA512.digest(binaryDocumentContent)));
		xdsDocument.setSize(Integer.toString(binaryDocumentContent.length));
		xdsDocument.setLanguageCode(languageCode);
		xdsDocument.setMimeType(mimeType);
		xdsDocument.setEncodingType(encodingType.actualName());
		xdsDocument.setFormatCode(formatCode);
		xdsDocument.setTypeCode(typeCode);
		xdsDocument.setCreationTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", ENGLISH).format(new Date(creationTime)));
		xdsDocument.setTitle(title);
		xdsDocument.setUniqueId(newUniqueId());
		xdsDocument.setRepositoryUniqueId(newUniqueId());
		xdsDocument.setEntryUUID(newUniqueId());
		xdsDocument.setPatientId(patientId(patientNhsNumber));
		xdsDocument.setAuthor(authors);

		if (parentDocumentUniqueId != null)
		{
			xdsDocument.setParentDocumentId(parentDocumentUniqueId.toString());
			assert parentDocumentRelationship != null;
			xdsDocument.setParentDocumentRelationship(parentDocumentRelationship.actualName());
		}

		if (comments != null)
		{
			xdsDocument.setComments(comments);
		}

		return xdsDocument;
	}

}
