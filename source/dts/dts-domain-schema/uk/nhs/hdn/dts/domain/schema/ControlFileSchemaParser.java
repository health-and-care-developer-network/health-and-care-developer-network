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

package uk.nhs.hdn.dts.domain.schema;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.xml.SchemaParser;
import uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.XRootXmlConstructor;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseException;
import uk.nhs.hdn.common.tuples.Pair;
import uk.nhs.hdn.dts.domain.ControlFile;
import uk.nhs.hdn.dts.domain.statusRecords.KnownStatusRecord;
import uk.nhs.hdn.dts.domain.statusRecords.StatusRecord;

import java.io.OutputStream;

import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.JavaObjectXmlConstructor.schemaFor;
import static uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.StringXmlConstructor.StringXmlConstructorInstance;
import static uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.XRootXmlConstructor.xml;
import static uk.nhs.hdn.common.serialisers.xml.XmlSerialiser.serialise;
import static uk.nhs.hdn.dts.domain.BooleanFlag.*;
import static uk.nhs.hdn.dts.domain.DataChecksum.UnknownDataChecksum;
import static uk.nhs.hdn.dts.domain.DtsName.UnknownDtsName;
import static uk.nhs.hdn.dts.domain.SmtpAddress.UnknownSmtpAddress;
import static uk.nhs.hdn.dts.domain.Subject.UnknownSubject;
import static uk.nhs.hdn.dts.domain.identifiers.DtsIdentifier.UnknownDtsIdentifier;
import static uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier.UnknownLocalIdentifier;
import static uk.nhs.hdn.dts.domain.identifiers.PartnerIdentifier.UnknownPartnerIdentifier;
import static uk.nhs.hdn.dts.domain.identifiers.ProcessIdentifier.UnknownProcessIdentifier;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.AddressTypeTextXmlConstructor.AddressTypeTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.BooleanFlagTextXmlConstructor.BooleanFlagTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.DataChecksumTextXmlConstructor.DataChecksumTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.DateTimeTextXmlConstructor.DateTimeTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.DtsIdentifierTextXmlConstructor.DtsIdentifierTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.DtsNameTextXmlConstructor.DtsNameTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.EventTextXmlConstructor.EventTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.LocalIdentifierTextXmlConstructor.LocalIdentifierTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.MessageTypeTextXmlConstructor.MessageTypeTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.PartnerIdentifierTextXmlConstructor.PartnerIdentifierTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.ProcessIdentifierTextXmlConstructor.ProcessIdentifierTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.SmtpAddressTextXmlConstructor.SmtpAddressTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.StatusCodeTextXmlConstructor.StatusCodeTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.StatusTextXmlConstructor.StatusTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.SubjectTextXmlConstructor.SubjectTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.VersionTextXmlConstructor.VersionTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.schema.xmlConstructors.WorkflowIdentifierTextXmlConstructor.WorkflowIdentifierTextXmlConstructorInstance;
import static uk.nhs.hdn.dts.domain.statusRecords.UnknownStatusRecord.UnknownStatusRecordInstance;

public final class ControlFileSchemaParser extends SchemaParser<ControlFile>
{
	@NotNull
	public static final XRootXmlConstructor<ControlFile> ControlFileSchema = xml
	(
		"DTSControl",
		false,
		schemaFor
		(
			ControlFile.class,
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
			nodeMayBeMissing("Compress", Y, BooleanFlagTextXmlConstructorInstance),
			nodeMayBeMissing("Encrypted", N, BooleanFlagTextXmlConstructorInstance),
			nodeMayBeMissing("IsCompressed", UnknownBooleanFlag, BooleanFlagTextXmlConstructorInstance),
			nodeMayBeMissing("DataChecksum", UnknownDataChecksum, DataChecksumTextXmlConstructorInstance),
			nodeMayBeMissing("PartnerIdentifier", UnknownPartnerIdentifier, PartnerIdentifierTextXmlConstructorInstance),
			nodeMayBeMissing("StatusRecord", UnknownStatusRecordInstance, schemaFor
			(
				StatusRecord.class,
				KnownStatusRecord.class,
				node("DateTime", DateTimeTextXmlConstructorInstance),
				node("Event", EventTextXmlConstructorInstance),
				node("Status", StatusTextXmlConstructorInstance),
				node("StatusCode", StatusCodeTextXmlConstructorInstance),
				node("Description", StringXmlConstructorInstance)
			))
		)
	);

	@NotNull
	public static final SchemaParser<ControlFile> ControlFileSchemaParserInstance = new ControlFileSchemaParser();

	@SuppressWarnings("unchecked")
	@NotNull
	private static final Pair<String, String>[] Empty = new Pair[0];

	public static void serialiseControlFile(@SuppressWarnings("TypeMayBeWeakened") @NotNull final ControlFile controlFile, @NotNull final OutputStream outputStream) throws CouldNotSerialiseException
	{
		serialise(false, "DTSControl", controlFile, outputStream, Utf8, Empty, Empty);
	}

	private ControlFileSchemaParser()
	{
		super(ControlFileSchema);
	}

}
