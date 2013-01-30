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

package uk.nhs.hdn.dts.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class SmtpAddress extends AbstractValueSerialisableStringIsUnknown implements Address
{
	@NotNull
	public static final SmtpAddress UnknownSmtpAddress = new SmtpAddress("");

	/*
	 There is no way to validate the entire variety of SMTP addresses using a Regex, although many try:-
	 	http://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
	 	http://www.regular-expressions.info/email.html

	 This is compounded by the problem of knowing which RFC version of SMTP addresses one is trying to be compatible with, everything from RFC2822 to RFC6531
	  */
	public SmtpAddress(@NonNls @NotNull final String assumedToBeValidSmtpAddress)
	{
		super(assumedToBeValidSmtpAddress);
	}
}
