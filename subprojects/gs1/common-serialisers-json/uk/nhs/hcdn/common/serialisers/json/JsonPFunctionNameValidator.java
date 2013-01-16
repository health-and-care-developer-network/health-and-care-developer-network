/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers.json;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import static java.lang.Character.*;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;

public final class JsonPFunctionNameValidator
{
	@NonNls
	private static final Collection<String> ReservedWords = new HashSet<>(asList(new String[]
	{
		"break", "do", "instanceof", "typeof", "case", "else", "new", "var", "catch", "finally",
		"return", "void", "continue", "for", "switch", "while", "debugger", "function", "this",
		"with", "default", "if", "throw", "delete", "in", "try", "class", "enum", "extends",
		"super", "const", "export", "import", "implements", "let", "private", "public", "yield",
		"interface", "package", "protected", "static", "null", "true", "false",
	}));
	private static final int Underscore = (int) '_';
	private static final int Dollar = (int) '$';
	private static final int ZeroWidthNonJoiner = 0x200C;
	private static final int ZeroWidthJoiner = 0x200D;
	private static final int MininimumSurrogate = 0xD800;
	private static final int MaximumSurrogatePlusOne = 0xDFFF + 1;
	private static final Pattern Dot = compile("\\.");

	@NotNull
	public static String validateJsonPFunctionName(@NotNull final String jsonPFunctionName) throws JsonFunctionNameInvalidException
	{
		if (jsonPFunctionName.isEmpty())
		{
			throw new JsonFunctionNameInvalidException(jsonPFunctionName);
		}
		final String[] functionNames = Dot.split(jsonPFunctionName);
		for (final String functionName : functionNames)
		{
			final int length = functionName.length();
			if (length == 0)
			{
				throw new JsonFunctionNameInvalidException("zero-length function names are invalid");
			}
			if (ReservedWords.contains(functionName))
			{
				throw new JsonFunctionNameInvalidException("there is a reserved word in the function name");
			}

			int index = 0;
			while(index < length)
			{
				final int codePoint = jsonPFunctionName.codePointAt(index);
				if (codePoint >= MininimumSurrogate && codePoint < MaximumSurrogatePlusOne)
				{
					throw new JsonFunctionNameInvalidException("of an invalid surrogate");
				}
				final int type = getType(codePoint);
				final boolean valid = index == 0 ? isValidCodePointForIndex0(codePoint, type) : isValidCodePointForSubsequentIndex(codePoint, type);
				if (!valid)
				{
					throw new JsonFunctionNameInvalidException("there is an invalid character in the function name");
				}
				index += isBmpCodePoint(codePoint) ? 1 : 2;
			}
			if (index == length + 1)
			{
				throw new JsonFunctionNameInvalidException("there is an unbalanced surrogate pair");
			}
		}
		return jsonPFunctionName;
	}

	private static boolean isValidCodePointForSubsequentIndex(final int codePoint, final int type)
	{
		return isValidCodePointForIndex0(codePoint, type) || is(codePoint, ZeroWidthNonJoiner) || is(codePoint, ZeroWidthJoiner) || isType(codePoint, NON_SPACING_MARK) || isType(codePoint, COMBINING_SPACING_MARK) || isType(codePoint, DECIMAL_DIGIT_NUMBER) || isType(codePoint, CONNECTOR_PUNCTUATION);
	}

	private static boolean isValidCodePointForIndex0(final int codePoint, final int type)
	{
		return is(codePoint, Dollar) || is(codePoint, Underscore) || isType(type, UPPERCASE_LETTER) || isType(type, LOWERCASE_LETTER) || isType(type, TITLECASE_LETTER) || isType(type, MODIFIER_LETTER) || isType(type, OTHER_LETTER) || isType(type, LETTER_NUMBER);
	}

	private static boolean is(final int codePoint, final int match)
	{
		return codePoint == match;
	}

	private static boolean isType(final int type, final byte category)
	{
		return type == (int) category;
	}

	private JsonPFunctionNameValidator()
	{
	}
}
