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

package uk.nhs.hcdn.barcodes.gs1.gs1Prefixes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.common.tuples.ComparablePair;

import static uk.nhs.hcdn.barcodes.Digit.Nine;
import static uk.nhs.hcdn.barcodes.Digit.Zero;
import static uk.nhs.hcdn.barcodes.Digits.digits;
import static uk.nhs.hcdn.common.IntegerHelper.power;

@SuppressWarnings("UnusedDeclaration")
public enum KnownGs1PrefixAssignment implements Gs1PrefixAssignment // some of these are RCNs, Restricted Circulation Numbers
{
	GS1_US("GS1 US", "000", "019", "030", "039", "060", "139"),
	Restricted_distribution_MO_defined("Restricted distribution (MO defined)", "020", "029", "040", "049", "200", "299"), // 200-299 is usually for internal use, varies by MO
	Must_Not_Be_Used_For_Internal_Applications("Must not be used for internal applications", "100", "199"), // http://helpdesk.gs1.org/ArticleDetails.aspx?GS1%20Identification%20Keys&id=b1fd1bf5-343a-e211-992c-00155d644635  KBA-01518-Y4S0Q5: How do I ensure I am using correctly assigned numbers?
	GS1_France("GS1 France", "300", "379"),
	GS1_Bulgaria("GS1 Bulgaria", "380"),
	GS1_Slovenija("GS1 Slovenija", "383"),
	GS1_Croatia("GS1 Croatia", "385"),
	GS1_BIH_Bosnia_Herzegovina("GS1 BIH (Bosnia-Herzegovina)", "387"),
	GS1_Montenegro("GS1 Montenegro", "389"),
	GS1_Germany("GS1 Germany", "400", "440"),
	GS1_Japan("GS1 Japan", "450", "459", "490", "499"),
	GS1_Russia("GS1 Russia", "460", "469"),
	GS1_Kyrgyzstan("GS1 Kyrgyzstan", "470"),
	GS1_Taiwan("GS1 Taiwan", "471"),
	GS1_Estonia("GS1 Estonia", "474"),
	GS1_Latvia("GS1 Latvia", "475"),
	GS1_Azerbaijan("GS1 Azerbaijan", "476"),
	GS1_Lithuania("GS1 Lithuania", "477"),
	GS1_Uzbekistan("GS1 Uzbekistan", "478"),
	GS1_Sri_Lanka("GS1 Sri Lanka", "479"),
	GS1_Philippines("GS1 Philippines", "480"),
	GS1_Belarus("GS1 Belarus", "481"),
	GS1_Ukraine("GS1 Ukraine", "482"),
	GS1_Moldova("GS1 Moldova", "484"),
	GS1_Armenia("GS1 Armenia", "485"),
	GS1_Georgia("GS1 Georgia", "486"),
	GS1_Kazakstan("GS1 Kazakstan", "487"),
	GS1_Tajikistan("GS1 Tajikistan", "488"),
	GS1_Hong_Kong("GS1 Hong Kong", "489"),
	GS1_UK("GS1 UK", "500", "509"),
	GS1_Association_Greece("GS1 Association Greece", "520", "521"),
	GS1_Lebanon("GS1 Lebanon", "528"),
	GS1_Cyprus("GS1 Cyprus", "529"),
	GS1_Albania("GS1 Albania", "530"),
	GS1_MAC_FYR_Macedonia("GS1 MAC (FYR Macedonia)", "531"),
	GS1_Malta("GS1 Malta", "535"),
	GS1_Ireland("GS1 Ireland", "539"),
	GS1_Belgium_Luxembourg("GS1 Belgium & Luxembourg", "540", "549"),
	GS1_Portugal("GS1 Portugal", "560"),
	GS1_Iceland("GS1 Iceland", "569"),
	GS1_Denmark("GS1 Denmark", "570", "579"),
	GS1_Poland("GS1 Poland", "590"),
	GS1_Romania("GS1 Romania", "594"),
	GS1_Hungary("GS1 Hungary", "599"),
	GS1_South_Africa("GS1 South Africa", "600", "601"),
	GS1_Ghana("GS1 Ghana", "603"),
	GS1_Senegal("GS1 Senegal", "604"),
	GS1_Bahrain("GS1 Bahrain", "608"),
	GS1_Mauritius("GS1 Mauritius", "609"),
	GS1_Morocco("GS1 Morocco", "611"),
	GS1_Algeria("GS1 Algeria", "613"),
	GS1_Nigeria("GS1 Nigeria", "615"),
	GS1_Kenya("GS1 Kenya", "616"),
	GS1_Ivory_Coast("GS1 Ivory Coast", "618"),
	GS1_Tunisia("GS1 Tunisia", "619"),
	GS1_Tanzania("GS1 Tanzania", "620"),
	GS1_Syria("GS1 Syria", "621"),
	GS1_Egypt("GS1 Egypt", "622"),
	GS1_Brunei("GS1 Brunei", "623"),
	GS1_Libya("GS1 Libya", "624"),
	GS1_Jordan("GS1 Jordan", "625"),
	GS1_Iran("GS1 Iran", "626"),
	GS1_Kuwait("GS1 Kuwait", "627"),
	GS1_Saudi_Arabia("GS1 Saudi Arabia", "628"),
	GS1_Emirates("GS1 Emirates", "629"),
	GS1_Finland("GS1 Finland", "640", "649"),
	GS1_China("GS1 China", "690", "695"),
	GS1_Norway("GS1 Norway", "700", "709"),
	GS1_Israel("GS1 Israel", "729"),
	GS1_Sweden("GS1 Sweden", "730", "739"),
	GS1_Guatemala("GS1 Guatemala", "740"),
	GS1_El_Salvador("GS1 El Salvador", "741"),
	GS1_Honduras("GS1 Honduras", "742"),
	GS1_Nicaragua("GS1 Nicaragua", "743"),
	GS1_Costa_Rica("GS1 Costa Rica", "744"),
	GS1_Panama("GS1 Panama", "745"),
	GS1_Republica_Dominicana("GS1 Republica Dominicana", "746"),
	GS1_Mexico("GS1 Mexico", "750"),
	GS1_Canada("GS1 Canada", "754", "755"),
	GS1_Venezuela("GS1 Venezuela", "759"),
	GS1_Schweiz_Suisse_Svizzera("GS1 Schweiz, Suisse, Svizzera", "760", "769"),
	GS1_Colombia("GS1 Colombia", "770", "771"),
	GS1_Uruguay("GS1 Uruguay", "773"),
	GS1_Peru("GS1 Peru", "775"),
	GS1_Bolivia("GS1 Bolivia", "777"),
	GS1_Argentina("GS1 Argentina", "778", "779"),
	GS1_Chile("GS1 Chile", "780"),
	GS1_Paraguay("GS1 Paraguay", "784"),
	GS1_Ecuador("GS1 Ecuador", "786"),
	GS1_Brasil("GS1 Brasil", "789", "790"),
	GS1_Italy("GS1 Italy", "800", "839"),
	GS1_Spain("GS1 Spain", "840", "849"),
	GS1_Cuba("GS1 Cuba", "850"),
	GS1_Slovakia("GS1 Slovakia", "858"),
	GS1_Czech("GS1 Czech", "859"),
	GS1_Serbia(" GS1 Serbia", "860"),
	GS1_Mongolia("GS1 Mongolia", "865"),
	GS1_North_Korea("GS1 North Korea", "867"),
	GS1_Turkey("GS1 Turkey", "868", "869"),
	GS1_Netherlands("GS1 Netherlands", "870", "879"),
	GS1_South_Korea("GS1 South Korea", "880"),
	GS1_Cambodia("GS1 Cambodia", "884"),
	GS1_Thailand("GS1 Thailand", "885"),
	GS1_Singapore("GS1 Singapore", "888"),
	GS1_India("GS1 India", "890"),
	GS1_Vietnam("GS1 Vietnam", "893"),
	GS1_Pakistan("GS1 Pakistan", "896"),
	GS1_Indonesia("GS1 Indonesia", "899"),
	GS1_Austria("GS1 Austria", "900", "919"),
	GS1_Australia("GS1 Australia", "930", "939"),
	GS1_New_Zealand("GS1 New Zealand", "940", "949"),
	GS1_Global_Office("GS1 Global Office", "950"),
	GS1_Global_Office_EPCglobal("GS1 Global Office (EPCglobal)", "951"),
	GS1_Malaysia("GS1 Malaysia", "955"),
	GS1_Macau("GS1 Macau", "958"),
	Global_Office_GTIN_8s("Global Office (GTIN-8s)", "960", "969"),
	Serial_publications_ISSN("Serial publications (ISSN)", "977"),
	Bookland_ISBN("Bookland (ISBN)", "978", "979"),
	Refund_receipts("Refund receipts", "980"),
	Common_Currency_Coupons("Common Currency Coupons", "981", "983"), // 981 == Euro; 982 apparently according to FAQ
	Coupons("Coupons", "050", "059", "990", "999"),
	;

	@SuppressWarnings({"ClassWithoutConstructor", "UtilityClassWithoutPrivateConstructor"})
	private static final class CompilerWorkaround
	{
		private static final int SizeOfDigit = 10;
		private static final Gs1PrefixAssignment[] Index = new Gs1PrefixAssignment[power(SizeOfDigit, 3)];
		static
		{
			for(int index = 0; index < 1000; index++)
			{
				Index[index] = new UnknownGs1PrefixAssignment(index);
			}
		}
	}

	@NotNull
	private final String actualName;

	KnownGs1PrefixAssignment(@NonNls @NotNull final String actualName, @NotNull final CharSequence single)
	{
		this(actualName, single, single);
	}

	KnownGs1PrefixAssignment(@NonNls @NotNull final String actualName, @NotNull final CharSequence from0, @NotNull final CharSequence to0)
	{
		this.actualName = actualName;
		index(new Gs1Prefix(digits(from0)).to(digits(to0)));
	}

	@SuppressWarnings("FeatureEnvy")
	KnownGs1PrefixAssignment(@NonNls @NotNull final String actualName, @NotNull final CharSequence from0, @NotNull final CharSequence to0, @NotNull final CharSequence from1, @NotNull final CharSequence to1)
	{
		this.actualName = actualName;
		index(new Gs1Prefix(digits(from0)).to(digits(to0)));
		index(new Gs1Prefix(digits(from1)).to(digits(to1)));
	}

	@SuppressWarnings("FeatureEnvy")
	KnownGs1PrefixAssignment(@NonNls @NotNull final String actualName, @NotNull final CharSequence from0, @NotNull final CharSequence to0, @NotNull final CharSequence from1, @NotNull final CharSequence to1, @NotNull final CharSequence from2, @NotNull final CharSequence to2)
	{
		this.actualName = actualName;
		index(new Gs1Prefix(digits(from0)).to(digits(to0)));
		index(new Gs1Prefix(digits(from1)).to(digits(to1)));
		index(new Gs1Prefix(digits(from2)).to(digits(to2)));
	}

	private void index(final ComparablePair<Gs1Prefix> pair)
	{
		final Gs1Prefix lowerBoundInclusive = pair.a;
		final int lowerBoundIndex = lowerBoundInclusive.to0To999();

		final Gs1Prefix upperBoundInclusive = pair.a;
		final int upperBoundIndex = upperBoundInclusive.to0To999();

		for(int index = lowerBoundIndex; index <= upperBoundIndex; index++)
		{
			if (CompilerWorkaround.Index[index] != null)
			{
				throw new IllegalStateException("ranges overlap");
			}
			CompilerWorkaround.Index[index] = this;
		}
	}

	@Override
	public boolean isISMN(@NotNull final Digit third, @NotNull final Digit fourth)
	{
		return this == Bookland_ISBN && third == Nine && fourth == Zero;
	}

	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	@Override
	@NotNull
	public String toString()
	{
		return actualName();
	}


	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static Gs1PrefixAssignment gs1PrefixAssignment(@NotNull final Gs1Prefix gs1Prefix)
	{
		return CompilerWorkaround.Index[gs1Prefix.to0To999()];
	}
}
