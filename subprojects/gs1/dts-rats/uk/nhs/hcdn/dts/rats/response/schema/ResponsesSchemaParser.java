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

package uk.nhs.hcdn.dts.rats.response.schema;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.xml.SchemaParser;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.XRootXmlConstructor;
import uk.nhs.hcdn.dts.domain.statusRecords.dateTime.DateTime;
import uk.nhs.hcdn.dts.rats.Message;
import uk.nhs.hcdn.dts.rats.response.Response;
import uk.nhs.hcdn.dts.rats.response.Status;
import uk.nhs.hcdn.dts.rats.response.details.Details;
import uk.nhs.hcdn.dts.rats.response.details.KnownDetails;

import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.ArrayXmlConstructor.arrayOf;
import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.JavaObjectXmlConstructor.schemaFor;
import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.StringXmlConstructor.StringXmlConstructorInstance;
import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.XRootXmlConstructor.xml;
import static uk.nhs.hcdn.dts.domain.Subject.UnknownSubject;
import static uk.nhs.hcdn.dts.domain.identifiers.DtsIdentifier.UnknownDtsIdentifier;
import static uk.nhs.hcdn.dts.domain.identifiers.ProcessIdentifier.UnknownProcessIdentifier;
import static uk.nhs.hcdn.dts.domain.identifiers.WorkflowIdentifier.UnknownWorkflowIdentifier;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.DtsIdentifierTextXmlConstructor.DtsIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.DtsNameTextXmlConstructor.DtsNameTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.LocalIdentifierTextXmlConstructor.LocalIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.ProcessIdentifierTextXmlConstructor.ProcessIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.SmtpAddressTextXmlConstructor.SmtpAddressTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.SubjectTextXmlConstructor.SubjectTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.WorkflowIdentifierTextXmlConstructor.WorkflowIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.schema.xmlConstructors.XmlSchemaDateTimeTextXmlConstructor.XmlSchemaDateTimeTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.domain.statusRecords.dateTime.UnknownDateTime.UnknownDateTimeInstance;
import static uk.nhs.hcdn.dts.rats.response.FailureDiagnostic.UnknownDiagnostic;
import static uk.nhs.hcdn.dts.rats.response.details.UnknownDetails.UnknownDetailsInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.FailureDiagnosticTextXmlConstructor.FailureDiagnosticTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.MessageIdentifierTextXmlConstructor.MessageIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.ResponseArrayCreator.ResponseArrayCreatorInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.ResponseStatusTextXmlConstructor.ResponseStatusTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.StatusValueTextXmlConstructor.StatusValueTextXmlConstructorInstance;

public final class ResponsesSchemaParser extends SchemaParser<Response[]>
{
	@NotNull
	public static final XRootXmlConstructor<Response[]> ResponsesSchema = xml
	(
		"DTSResponse",
		false,
		arrayOf
		(
			"Response",
			ResponseArrayCreatorInstance,
			schemaFor
			(
				Response.class,
				node("Message", schemaFor
				(
					Message.class,
					node("FromDTSName", DtsNameTextXmlConstructorInstance),
					node("LocalID", LocalIdentifierTextXmlConstructorInstance)
				)),
				node("ResponseStatus", ResponseStatusTextXmlConstructorInstance),
				nodeMayBeMissing("Details", UnknownDetailsInstance, schemaFor
				(
					Details.class,
					KnownDetails.class,
					node("MsgID", MessageIdentifierTextXmlConstructorInstance),
					node("ToDTSName", DtsNameTextXmlConstructorInstance),
					node("FromSMTPAddr", SmtpAddressTextXmlConstructorInstance),
					node("ToSMTPAddr", SmtpAddressTextXmlConstructorInstance),
					node("TransferDateTime", XmlSchemaDateTimeTextXmlConstructorInstance),
					node("MsgReport", StringXmlConstructorInstance),
					node("Status", schemaFor
					(
						Status.class,
						node("StatusVal", StatusValueTextXmlConstructorInstance),
						node("StatusText", StringXmlConstructorInstance)
					)),
					nodeMayBeMissing("SentDateTime", DateTime.class, UnknownDateTimeInstance, XmlSchemaDateTimeTextXmlConstructorInstance),

					nodeMayBeMissing("FailureDateTime", DateTime.class, UnknownDateTimeInstance, XmlSchemaDateTimeTextXmlConstructorInstance),
					nodeMayBeMissing("FailureDiagnostic", UnknownDiagnostic, FailureDiagnosticTextXmlConstructorInstance),
					nodeMayBeMissing("FailureMsgDTSID", UnknownDtsIdentifier, DtsIdentifierTextXmlConstructorInstance),

					nodeMayBeMissing("Subject", UnknownSubject, SubjectTextXmlConstructorInstance),
					nodeMayBeMissing("WorkflowID", UnknownWorkflowIdentifier, WorkflowIdentifierTextXmlConstructorInstance),
					nodeMayBeMissing("ProcessID", UnknownProcessIdentifier, ProcessIdentifierTextXmlConstructorInstance)
				))
			)
		)
	);

	@NotNull
	public static final SchemaParser<Response[]> ResponsesSchemaParserInstance = new ResponsesSchemaParser();

	private ResponsesSchemaParser()
	{
		super(ResponsesSchema);
	}
}
