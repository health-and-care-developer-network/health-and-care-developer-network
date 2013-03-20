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

package uk.nhs.hdn.ihe.xds.client;

import org.jetbrains.annotations.NotNull;
import org.openhealthtools.ihe.bridge.conf.SessionContext;
import org.openhealthtools.ihe.bridge.ws.OHFBridge;
import uk.nhs.hdn.number.NhsNumber;

import static java.lang.System.currentTimeMillis;
import static org.openhealthtools.ihe.bridge.conf.SessionContext.DEFAULT;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.ihe.xds.builders.AuthorBuilder.author;
import static uk.nhs.hdn.ihe.xds.builders.AuthorInstitutionBuilder.authorInstitution;
import static uk.nhs.hdn.ihe.xds.builders.CodedMetadataBuilder.codedMetadata;
import static uk.nhs.hdn.ihe.xds.builders.EncodingType.identity;
import static uk.nhs.hdn.ihe.xds.builders.GenericPnRSubmissionSetBuilder.genericPnRSubmissionSet;
import static uk.nhs.hdn.ihe.xds.builders.GenericProvideAndRegisterBuilder.genericProvideAndRegister;
import static uk.nhs.hdn.ihe.xds.builders.PatientInfoBuilder.patientInfo;
import static uk.nhs.hdn.ihe.xds.builders.PatientNameBuilder.patientName;
import static uk.nhs.hdn.ihe.xds.builders.XdsDocumentBuilder.xdsDocument;
import static uk.nhs.hdn.number.NhsNumber.valueOf;

public final class IheXdsClientConsoleEntryPoint
{
	// http://sourceforge.net/projects/iheos/
	public static void main(@NotNull final String... commandLineArguments) throws Throwable
	{
		final OHFBridge ohfBridge = new OHFBridge();

		final SessionContext sessionContext = DEFAULT;

		final byte[] data = "Hello World".getBytes(Utf8);
		final NhsNumber patientNhsNumber = valueOf("1234567881");

		ohfBridge.SubmitDocument(sessionContext, genericProvideAndRegister
		(
			genericPnRSubmissionSet(codedMetadata("content-type-y").code("submission-x").displayName("content-type-y-submission-x")).patientId(patientNhsNumber),
			xdsDocument(data, "EN", "text/plain;charset=utf-8", identity, codedMetadata("format-code").code("version-1").displayName("format-code-version-1"), codedMetadata("type-code").code("notes-code").displayName("type-code-notes"), currentTimeMillis(), "Hello World Notes", patientNhsNumber, author(patientInfo(patientName("Kelly").givenName("Mike"))).specialities("clinical negligence").institutions(authorInstitution("Broadmoor")).roles("document author")).build()
		));
	}

	/*

		sudo apt-get install glassfish-* postgresql

		sudo passwd postgres
		su - postgres

		In psql:-
		CREATE ROLE adt WITH CREATEDB CREATEROLE LOGIN PASSWORD 'xdsadt';
		CREATE ROLE log WITH CREATEDB CREATEROLE LOGIN PASSWORD 'xdslog';
		CREATE ROLE repo WITH CREATEDB CREATEROLE LOGIN PASSWORD 'xdsrepo';
		CREATE ROLE omar WITH CREATEDB CREATEROLE LOGIN PASSWORD 'xdsomar';
		«HIEOS_ROOT»\data\adt\postgres\createadtddl.sql
		«HIEOS_ROOT»\data\log\postgres\createlogddl.sql
		«HIEOS_ROOT»\data\repo\postgres\createrepoddl.sql
		«HIEOS_ROOT»\data\registry\createregistryddl.sql




		Connection Pool Configuration

Copy the JDBC driver JAR file, «HIEOS_ROOT»\lib\postgresql-8.3-604.jdbc4.jar, to «GLASSFISH_HOME»\domains\domain1\lib\ext
Restart GlassFish and log on to the admin console (e.g. http://localhost:4848)
Set up connection pools by going to Resources->JDBC->Connection Pool
For the "adt" data store
Select “New”
Enter Name: hieos-adt-pool
Enter Resource Type: javax.sql.ConnectionPoolDataSource
Enter Vendor: PostgreSQL
Click “Next”
Delete all properties from the “Additional Properties section” except User, Password, ServerName, DatabaseName and PortNumber
Set User: adt, Password: xdsadt
Set DatabaseName: adt, ServerName: localhost, PortNumber: 5432
Click “Finish”
Select the newly created connection pool and click “Ping”
For the "log" data store
Select “New”
Enter Name: hieos-log-pool
Enter Resource Type: javax.sql.ConnectionPoolDataSource
Enter Vendor: PostgreSQL
Click “Next”
Delete all properties from the “Additional Properties section” except User, Password, ServerName, DatabaseName and PortNumber
Set User: log, Password: xdslog
Set DatabaseName: log, ServerName: localhost, PortNumber: 5432
Click “Finish”
Select the newly created connection pool and click “Ping”
For the "omar" data store
Select “New”
Enter Name: hieos-registry-pool
Enter Resource Type: javax.sql.ConnectionPoolDataSource
Enter Vendor: PostgreSQL.
Click “Next”
Delete all properties from the “Additional Properties section” except User, Password, ServerName, DatabaseName and PortNumber
Set User: omar, Password: omar
Set DatabaseName: omar, ServerName: localhost, PortNumber: 5432
Click “Finish”
Select the newly created connection pool and click “Ping”
For the "repo" data store
Select “New”
Enter Name: hieos-repo-pool
Enter Resource Type: javax.sql.ConnectionPoolDataSource
Enter Vendor: PostgreSQL
Click “Next”
Delete all properties from the “Additional Properties section” except User, Password, ServerName, DatabaseName and PortNumber
Set User: repo, Password: xdsrepo
Set DatabaseName: repo, ServerName: localhost, PortNumber: 5432
Click “Finish”
Select the newly created connection pool and click “Ping”
	 */
}
