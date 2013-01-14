/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.keys;

import uk.nhs.hcdn.barcodes.gs1.checkDigits.ExtractingCheckDigit;
import uk.nhs.hcdn.common.naming.ActualName;
import uk.nhs.hcdn.common.naming.FormerActualNames;

public interface KeyFormat extends ActualName, FormerActualNames, ExtractingCheckDigit
{
	int size();
}
