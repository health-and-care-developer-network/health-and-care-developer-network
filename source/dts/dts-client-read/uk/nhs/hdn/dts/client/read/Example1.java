package uk.nhs.hdn.dts.client.read;

import org.xml.sax.SAXException;
import uk.nhs.hdn.dts.domain.ControlFile;
import uk.nhs.hdn.dts.domain.identifiers.WorkflowIdentifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static uk.nhs.hdn.dts.domain.schema.ControlFileSchemaParser.ControlFileSchemaParserInstance;

public class Example1
{
	public void example() throws IOException, SAXException
	{
		final String filePath = "/PATH/TO/CONTROL/FILE";
		final InputStream inputStream = new FileInputStream(filePath);
		final ControlFile controlFile;
		try
		{
			controlFile = ControlFileSchemaParserInstance.parse(inputStream);
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException ignored)
			{
			}
		}

		final WorkflowIdentifier workflowIdentifier = controlFile.workflowIdentifier;
	}
}
