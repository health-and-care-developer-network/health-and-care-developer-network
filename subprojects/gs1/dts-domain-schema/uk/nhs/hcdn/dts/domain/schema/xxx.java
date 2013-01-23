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

import org.xml.sax.SAXException;
import uk.nhs.hcdn.dts.domain.ControlFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class xxx
{
	public static void main(String[] args) throws IOException, SAXException
	{
		final byte[] bytes = x.getBytes("UTF-8");
		final ControlFile parse = ControlFileSchemaParser.ControlFileSchemaParserInstance.parse(new ByteArrayInputStream(bytes));
		System.out.println("parse = " + parse);
	}

	private static final String x = "<DTSControl>\n" +
			"  <Version>1.0</Version>\n" +
			"  <AddressType>ALL</AddressType>\n" +
			"  <MessageType>Data</MessageType>\n" +
			"  <From_ESMTP>phoenx1hc1@dts.nhs.uk</From_ESMTP>\n" +
			"  <From_DTS>phoenx1HC1</From_DTS>\n" +
			"  <To_ESMTP>testgp2gp1@dts.nhs.uk</To_ESMTP>\n" +
			"  <To_DTS>testgp2gp1</To_DTS>\n" +
			"  <Subject>SystmOne Weekly GP2GP Report v2_2</Subject>\n" +
			"  <LocalId>Gp2GpMiReportGp2Gp Weekly Reports v2_1 Stats3.csv</LocalId>\n" +
			"  <DTSId>dtsapp05-201301201947406678</DTSId>\n" +
			"  <WorkflowId>GP2GP_MI</WorkflowId>\n" +
			"  <ProcessId></ProcessId>\n" +
			"  <Compress>Y</Compress>\n" +
			"  <Encrypted>N</Encrypted>\n" +
			"  <DataChecksum></DataChecksum>\n" +
			"  <IsCompressed>N</IsCompressed>\n" +
			"  <StatusRecord>\n" +
			"    <DateTime>20130120194740</DateTime>\n" +
			"    <Event>TRANSFER</Event>\n" +
			"    <Status>SUCCESS</Status>\n" +
			"    <StatusCode>00</StatusCode>\n" +
			"    <Description>Transferred to recipient mailbox</Description>\n" +
			"  </StatusRecord>\n" +
			"</DTSControl>\n";
}
