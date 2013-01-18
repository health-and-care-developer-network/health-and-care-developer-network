/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client;

import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.GenericJsonParseEventHandler;

import java.io.StringReader;

public class Experiment
{
	public static void main(String[] args) throws InvalidJsonException
	{
		final StringReader in = new StringReader("[{\"hello\"  :  \"abc\"}]");

		final Object o = GenericJsonParseEventHandler.genericParse(in);
		System.out.println("o = " + o);
	}
}
