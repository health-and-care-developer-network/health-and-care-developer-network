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

package uk.nhs.hdn.barcodes.gs1;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.naming.ActualName;
import uk.nhs.hdn.common.naming.FormerActualNames;

import java.util.Set;

import static uk.nhs.hdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum ApplicationIdentifier implements ActualName, FormerActualNames
{
	SerialShippingContainerCode("Serial Shipping Container Code", "00"),
	GlobalTradeItemNumber("Global Trade Item Number", "01"),
	GlobalReturnableAssetIdentifier("Global Returnable Asset Identifier", "8003"),
	GlobalDocumentTypeIdentifier("Global Document Type Identifier","253"),

	BatchNumber("Batch Number", "10"),
	ExpirationDate("Expiration Date", "17"),
	SerialNumber("Serial Number", "21"),
	DeliverToLocation("Deliver To Location AI", "410"),
	InvoiceToLocation("Invoice To Location AI", "411"),
	PurchasedFromLocation("Purchased From Location AI", "412"),
	ShipToDeliverToForwardToLocation("Ship To - Deliver To - Foward To Location AI", "413"),
	PhysicalLocation("Physical Location AI", "414"),
	LocationNumberOfTheInvoicingParty("Location Number of the Invoicing Party AI", "415"),
	ExpirationDateAndTime("Expiration Date and Time", "7003"),
	;

	@NotNull
	private final String actualName;
	private final Set<String> formerActualNames;

	ApplicationIdentifier(@NotNull @NonNls final String actualName, @NotNull final String identifier, @NotNull final String... formerActualNames)
	{
		this.actualName = actualName;
		this.formerActualNames = unmodifiableSetOf(formerActualNames);
	}

	@NonNls
	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return formerActualNames;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return actualName;
	}
}
