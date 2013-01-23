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

package uk.nhs.hcdn.dts.domain.schema;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.xml.SchemaParser;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.*;
import uk.nhs.hcdn.common.tuples.Pair;
import uk.nhs.hcdn.dts.domain.ControlFile;
import uk.nhs.hcdn.dts.domain.statusRecords.KnownStatusRecord;

import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.JavaObjectXmlConstructor.schemaFor;
import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.StringXmlConstructor.StringXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.BooleanFlag.UnknownBooleanFlag;
import static uk.nhs.hcdn.dts.domain.DataChecksum.UnknownDataChecksum;
import static uk.nhs.hcdn.dts.domain.DtsName.UnknownDtsName;
import static uk.nhs.hcdn.dts.domain.SmtpAddress.UnknownSmtpAddress;
import static uk.nhs.hcdn.dts.domain.Subject.UnknownSubject;
import static uk.nhs.hcdn.dts.domain.identifiers.DtsIdentifier.UnknownDtsIdentifier;
import static uk.nhs.hcdn.dts.domain.identifiers.LocalIdentifier.UnknownLocalIdentifier;
import static uk.nhs.hcdn.dts.domain.identifiers.ProcessIdentifier.UnknownProcessIdentifier;
import static uk.nhs.hcdn.dts.domain.schema.AddressTypeTextXmlConstructor.AddressTypeTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.BooleanFlagTextXmlConstructor.BooleanFlagTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.DataChecksumTextXmlConstructor.DataChecksumTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.DateTimeTextXmlConstructor.DateTimeTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.DtsIdentifierTextXmlConstructor.DtsIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.DtsNameTextXmlConstructor.DtsNameTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.EventTextXmlConstructor.EventTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.LocalIdentifierTextXmlConstructor.LocalIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.MessageTypeTextXmlConstructor.MessageTypeTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.ProcessIdentifierTextXmlConstructor.ProcessIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.SmtpAddressTextXmlConstructor.SmtpAddressTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.StatusCodeTextXmlConstructor.StatusCodeTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.StatusTextXmlConstructor.StatusTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.SubjectTextXmlConstructor.SubjectTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.VersionTextXmlConstructor.VersionTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.WorkflowIdentifierTextXmlConstructor.WorkflowIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.statusRecords.UnknownStatusRecord.UnknownStatusRecordInstance;

public final class ControlFileSchemaParser extends SchemaParser<ControlFile>
{
	@NotNull
	public static final XmlConstructor<?, ControlFile> ControlFileSchema = schemaFor
	(
		ControlFile.class,
		node("Version", VersionTextXmlConstructorInstance),
		node("AddressType", AddressTypeTextXmlConstructorInstance),
		node("MessageType", MessageTypeTextXmlConstructorInstance),
		node("WorkflowId", WorkflowIdentifierTextXmlConstructorInstance),
		nodeMayBeMissing("From_ESMTP", UnknownSmtpAddress, SmtpAddressTextXmlConstructorInstance),
		nodeMayBeMissing("From_DTS", UnknownDtsName, DtsNameTextXmlConstructorInstance),
		nodeMayBeMissing("To_ESMTP", UnknownSmtpAddress, SmtpAddressTextXmlConstructorInstance),
		nodeMayBeMissing("To_DTS", UnknownDtsName, DtsNameTextXmlConstructorInstance),
		nodeMayBeMissing("Subject", UnknownSubject, SubjectTextXmlConstructorInstance),
		nodeMayBeMissing("LocalId", UnknownLocalIdentifier, LocalIdentifierTextXmlConstructorInstance),
		nodeMayBeMissing("DTSId", UnknownDtsIdentifier, DtsIdentifierTextXmlConstructorInstance),
		nodeMayBeMissing("ProcessId", UnknownProcessIdentifier, ProcessIdentifierTextXmlConstructorInstance),
		nodeMayBeMissing("Compress", UnknownBooleanFlag, BooleanFlagTextXmlConstructorInstance),
		nodeMayBeMissing("Encrypted", UnknownBooleanFlag, BooleanFlagTextXmlConstructorInstance),
		nodeMayBeMissing("DataChecksum", UnknownDataChecksum, DataChecksumTextXmlConstructorInstance),
		nodeMayBeMissing("IsCompressed", UnknownBooleanFlag, BooleanFlagTextXmlConstructorInstance),
		nodeMayBeMissing("StatusRecord", UnknownStatusRecordInstance, schemaFor
		(
			KnownStatusRecord.class,
			node("DateTime", DateTimeTextXmlConstructorInstance),
			node("Event", EventTextXmlConstructorInstance),
			node("Status", StatusTextXmlConstructorInstance),
			node("StatusCode", StatusCodeTextXmlConstructorInstance),
			node("Description", StringXmlConstructorInstance)
		))
	);

	@NotNull
	public static final SchemaParser<ControlFile> ControlFileSchemaParser = new ControlFileSchemaParser();

	private ControlFileSchemaParser()
	{
		super(ControlFileSchema);
	}

	@SuppressWarnings("unchecked")
	private static <C, V, X, Y extends V> Pair<String, MissingFieldXmlConstructor<?, ?>> nodeMayBeMissing(@NonNls final String value, @Nullable final X valueIfMissing, final XmlConstructor<C, Y> xmlConstructor)
	{
		return new Pair<String, MissingFieldXmlConstructor<?, ?>>(value, new MayBeMissingFieldXmlConstructor(xmlConstructor, valueIfMissing));
	}

	private static <C, V> Pair<String, MissingFieldXmlConstructor<?, ?>> node(@NonNls @NotNull final String value, @NotNull final XmlConstructor<C, V> xmlConstructor)
	{
		return new Pair<String, MissingFieldXmlConstructor<?, ?>>(value, new RequiredMissingFieldXmlConstructor<>(xmlConstructor));
	}
}
