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

package uk.nhs.hcdn.dts.rats.response;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.xml.SchemaParser;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.*;
import uk.nhs.hcdn.common.tuples.Pair;
import uk.nhs.hcdn.dts.rats.Message;
import uk.nhs.hcdn.dts.rats.response.details.Details;
import uk.nhs.hcdn.dts.rats.response.details.KnownDetails;

import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.ArrayRootXmlConstructor.rootSchemaFor;
import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.IntegerXmlConstructor.IntegerXmlConstructorInstance;
import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.JavaObjectXmlConstructor.schemaFor;
import static uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.StringXmlConstructor.StringXmlConstructorInstance;
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
import static uk.nhs.hcdn.dts.rats.response.ResponseArrayCreator.ResponseArrayCreatorInstance;
import static uk.nhs.hcdn.dts.rats.response.details.UnknownDetails.UnknownDetailsInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.FailureDiagnosticTextXmlConstructor.FailureDiagnosticTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.MessageIdentifierTextXmlConstructor.MessageIdentifierTextXmlConstructorInstance;
import static uk.nhs.hcdn.dts.rats.response.schema.ResponseStatusTextXmlConstructor.ResponseStatusTextXmlConstructorInstance;

public final class ResponsesSchemaParser extends SchemaParser<Response, Response[]>
{
	@NotNull
	public static final AbstractRootXmlConstructor<Response, Response[]> ResponsesSchema = rootSchemaFor
	(
		"DTSResponse",
		false,
		ResponseArrayCreatorInstance,
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
				node("StatusVal", IntegerXmlConstructorInstance),
				node("StatusText", StringXmlConstructorInstance)
			)),
			nodeMayBeMissing("SentDateTime", UnknownDateTimeInstance, XmlSchemaDateTimeTextXmlConstructorInstance),

			nodeMayBeMissing("FailureDateTime", UnknownDateTimeInstance, XmlSchemaDateTimeTextXmlConstructorInstance),
			nodeMayBeMissing("FailureDiagnostic", UnknownDiagnostic, FailureDiagnosticTextXmlConstructorInstance),
			nodeMayBeMissing("FailureMsgDTSID", UnknownDtsIdentifier, DtsIdentifierTextXmlConstructorInstance),

			nodeMayBeMissing("Subject", UnknownSubject, SubjectTextXmlConstructorInstance),
			nodeMayBeMissing("WorkflowID", UnknownWorkflowIdentifier, WorkflowIdentifierTextXmlConstructorInstance),
			nodeMayBeMissing("ProcessID", UnknownProcessIdentifier, ProcessIdentifierTextXmlConstructorInstance)
		))
	);

	@NotNull
	public static final SchemaParser<Response, Response[]> ResponsesSchemaParserInstance = new ResponsesSchemaParser();

	private ResponsesSchemaParser()
	{
		super(ResponsesSchema);
	}

	@SuppressWarnings("unchecked")
	private static <C, V, X, Y extends V> Pair<String, MissingFieldXmlConstructor<?, ?>> nodeMayBeMissing(@NonNls final String value, @NotNull final X valueIfMissing, final XmlConstructor<C, Y> xmlConstructor)
	{
		return new Pair<String, MissingFieldXmlConstructor<?, ?>>(value, new MayBeMissingFieldXmlConstructor(xmlConstructor, valueIfMissing));
	}

	private static <C, V> Pair<String, MissingFieldXmlConstructor<?, ?>> node(@NonNls @NotNull final String value, @NotNull final XmlConstructor<C, V> xmlConstructor)
	{
		return new Pair<String, MissingFieldXmlConstructor<?, ?>>(value, new RequiredMissingFieldXmlConstructor<>(xmlConstructor));
	}

}
