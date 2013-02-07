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

package uk.nhs.hdn.common.postCodes.codes.outward;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.postCodes.BritishForcesPostOfficePostCode;
import uk.nhs.hdn.common.postCodes.codes.AbstractCode;
import uk.nhs.hdn.common.postCodes.codes.inward.BritishForcesPostOfficeInwardCode;

public final class BritishForcesPostOfficeOutwardCode extends AbstractCode implements OutwardCode<BritishForcesPostOfficePostCode, BritishForcesPostOfficeInwardCode>
{
	@NonNls
	@NotNull
	public static final String BFPO = "BFPO";

	@NotNull
	public static final BritishForcesPostOfficeOutwardCode BritishForcesPostOfficeOutwardCodeInstance = new BritishForcesPostOfficeOutwardCode();

	private BritishForcesPostOfficeOutwardCode()
	{
		super(BFPO);
	}

	@Override
	@NotNull
	public BritishForcesPostOfficePostCode with(@NotNull final BritishForcesPostOfficeInwardCode inwardCode)
	{
		return new BritishForcesPostOfficePostCode(this, inwardCode);
	}
}
