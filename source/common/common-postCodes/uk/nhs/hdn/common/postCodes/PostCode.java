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

package uk.nhs.hdn.common.postCodes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.charaterSets.BitSetCharacterSet;
import uk.nhs.hdn.common.parsers.charaterSets.CharacterSet;

public interface PostCode
{
	int Ascii0 = 48;
	int Ascii9 = 57;
	@NotNull
	CharacterSet ValidNumericCharacters = new BitSetCharacterSet(Ascii0, Ascii9);
	int AsciiA = 65;
	int AsciiZ = 90;
	@NotNull
	CharacterSet ValidAlphabeticCharacters = new BitSetCharacterSet(AsciiA, AsciiZ);
	@NonNls
	@NotNull
	CharacterSet ValidTrailingAlphabeticCharacters = new BitSetCharacterSet("ABCDEFGHJKMNPRSTUVWXY".toCharArray()); // excludes ILOQZ
	@NotNull
	@NonNls
	CharacterSet ValidAlphabeticPostcodeUnitCharacters = new BitSetCharacterSet("ABDEFGHJLNPQRSTUWXYZ".toCharArray()); // excludes CIKMOV

	@NotNull
	String normalised();
}
