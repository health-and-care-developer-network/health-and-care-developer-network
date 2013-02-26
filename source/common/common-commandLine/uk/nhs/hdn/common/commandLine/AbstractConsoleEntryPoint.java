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

package uk.nhs.hdn.common.commandLine;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.naming.Description;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.Locale.ENGLISH;
import static java.util.regex.Pattern.compile;
import static uk.nhs.hdn.common.commandLine.ExitCode.*;

public abstract class AbstractConsoleEntryPoint extends AbstractToString
{
	private static final char Underscore = '_';
	private static final char Hyphen = '-';
	private static final Pattern CamelCaseSplit = compile("(?<!^)(?=[A-Z])");
	@NonNls @NotNull private static final String ConsoleEntryPoint = "ConsoleEntryPoint";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static <C extends AbstractConsoleEntryPoint> void execute(@NotNull final Class<C> consoleEntryPoint, @NotNull final String... commandLineArguments)
	{
		final C consoleEntryPointInstance;
		try
		{
			consoleEntryPointInstance = consoleEntryPoint.getConstructor().newInstance();
		}
		catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		execute(consoleEntryPointInstance, commandLineArguments);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void execute(@NotNull final AbstractConsoleEntryPoint consoleEntryPoint, @NotNull final String... commandLineArguments)
	{
		consoleEntryPoint.execute(commandLineArguments);
	}

	private static final int MaximumPortNumber = 65535;

	@NotNull private static final String HelpOption = "help";
	@NotNull private static final String VersionOption = "version";

	@NotNull private final PrintStream outputStream;
	@NotNull private final PrintStream errorStream;
	@NotNull private final OptionParser options;
	private final boolean doesNotHaveNonOptionArguments;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	protected AbstractConsoleEntryPoint()
	{
		outputStream = out;
		errorStream = err;
		options = new OptionParser();
		options.accepts(HelpOption, "Displays help for options").forHelp();
		options.accepts(VersionOption, "Displays version");
		doesNotHaveNonOptionArguments = options(options);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	public static <E extends Enum<E>> String enumDescription(@NotNull final E[] enumValues, final boolean prependDoubleDash, final boolean convertUnderscoresBecauseTheyAreNotValidInLongOptionNames)
	{
		final StringBuilder stringBuilder = new StringBuilder("One of ");
		final int length = enumValues.length;
		final int lastIndex = length - 1;
		boolean isAfterFirst = false;
		for(int index = 0; index < length; index++)
		{
			if (isAfterFirst)
			{
				if (index == lastIndex)
				{
					stringBuilder.append(" or ");
				}
				else
				{
					stringBuilder.append(", ");
				}
			}
			else
			{
				isAfterFirst = true;
			}
			if (prependDoubleDash)
			{
				stringBuilder.append("--");
			}
			stringBuilder.append(convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(convertUnderscoresBecauseTheyAreNotValidInLongOptionNames, enumValues[index]));
		}
		return stringBuilder.toString();
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	public static <E extends Enum<E>> void oneOfEnumAsOption(@NotNull final OptionParser options, @NotNull final E[] enumValues)
	{
		for (final E enumValue : enumValues)
		{
			options.accepts(convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, enumValue), convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, enumValue));
		}
	}

	public static <E extends Enum<E> & Description> void oneOfEnumAsOptionWithRequiredArgument(@NotNull final OptionParser options, @NotNull final E[] enumValues, @NotNull final Class<?> ofType)
	{
		for (final E enumValue : enumValues)
		{
			options.accepts(convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, enumValue), convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, enumValue)).withRequiredArg().ofType(ofType).describedAs(enumValue.description());
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	public <E extends Enum<E>> E enumOptionChosen(@NotNull final OptionSet optionSet, @NotNull final E[] enumValues)
	{
		@Nullable E enumValueFound = null;
		for (final E enumValue : enumValues)
		{
			if (optionSet.has(convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, enumValue)))
			{
				if (enumValueFound != null)
				{
					exitWithErrorAndHelp("One, and only one, of " + enumDescription(enumValues, true, true), "must be provided");
					throw new ShouldHaveExitedException();
				}
				enumValueFound = enumValue;
			}
		}
		if (enumValueFound == null)
		{
			exitWithErrorAndHelp("One, and only one, of " + enumDescription(enumValues, true, true), "must be provided");
			throw new ShouldHaveExitedException();
		}
		return enumValueFound;
	}

	@NotNull
	public static String convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(final boolean convertUnderscoresBecauseTheyAreNotValidInLongOptionNames, @NotNull final Enum<?> enumValue)
	{
		return convertUnderscoresInValueAsTheyAreNotValidForLongOptions(convertUnderscoresBecauseTheyAreNotValidInLongOptionNames, enumValue.name());
	}

	@NotNull
	public static String convertUnderscoresInValueAsTheyAreNotValidForLongOptions(final boolean convertUnderscoresBecauseTheyAreNotValidInLongOptionNames, @NotNull final String value)
	{
		return convertUnderscoresBecauseTheyAreNotValidInLongOptionNames ? value.replace(Underscore, Hyphen) : value;
	}

	@NotNull
	public static String reverseConversionOfUnderscoresInValueAsTheyAreNotValidForLongOptions(@NotNull final String value)
	{
		return value.replace(Hyphen, Underscore);
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	protected abstract boolean options(@NotNull final OptionParser options);

	@SuppressWarnings("ProhibitedExceptionDeclared")
	protected abstract void execute(@NotNull final OptionSet optionSet) throws Exception;

	public final void exitWithErrorAndHelp(@NotNull @NonNls final String optionName, @NotNull final Object optionValue, @NotNull @NonNls final String must)
	{
		exitWithErrorAndHelp(format(ENGLISH, "--%1$s %2$s %3$s", optionName, optionValue, must));
	}

	public final void exitWithErrorAndHelp(@NonNls @NotNull final String template, @NonNls @NotNull final Object... values)
	{
		exitWithErrorAndHelp(format(ENGLISH, template, values));
	}

	@SuppressWarnings({"MethodMayBeStatic", "QuestionableName", "unchecked"})
	@NotNull
	public final <S> S defaulted(@NotNull final OptionSet optionSet, @NonNls @NotNull final String optionName)
	{
		return (S) optionSet.valueOf(optionName);
	}

	@SuppressWarnings({"MethodMayBeStatic", "QuestionableName", "unchecked"})
	@NotNull
	public final <S> S required(@NotNull final OptionSet optionSet, @NonNls @NotNull final String optionName)
	{
		if (!optionSet.has(optionName))
		{
			exitWithErrorAndHelp(optionName, "", "must be provided");
			throw new ShouldHaveExitedException();
		}
		return (S) optionSet.valueOf(optionName);
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	public final char portNumber(@NotNull final OptionSet optionSet, @NonNls @NotNull final String optionName)
	{
		final int port = (Integer) optionSet.valueOf(optionName);
		if (port < 1 || port > MaximumPortNumber)
		{
			exitWithErrorAndHelp(optionName, port, format(ENGLISH, "must be between 1 and %1$s inclusive", MaximumPortNumber));
			throw new ShouldHaveExitedException();
		}
		return (char) port;
	}

	public final int unsignedInteger(@NotNull final OptionSet optionSet, @NonNls @NotNull final String optionName)
	{
		final int integer = (Integer) optionSet.valueOf(optionName);
		if (integer < 0)
		{
			exitWithErrorAndHelp(optionName, integer, "must be positive");
			throw new ShouldHaveExitedException();
		}
		return integer;
	}

	@NotNull
	public final File readableDirectory(@NotNull final OptionSet optionSet, @NonNls @NotNull final String optionName)
	{
		final File dataPath = (File) defaulted(optionSet, optionName);
		if (!(dataPath.exists() && dataPath.canRead() && dataPath.isDirectory()))
		{
			exitWithErrorAndHelp(optionName, dataPath, "does not exist as a readable directory");
			throw new ShouldHaveExitedException();
		}
		return dataPath;
	}

	@NotNull
	public final File writableDirectory(@NotNull final OptionSet optionSet, @NonNls @NotNull final String optionName)
	{
		final File dataPath = (File) optionSet.valueOf(optionName);
		if (!(dataPath.exists() && dataPath.canWrite() && dataPath.isDirectory()))
		{
			exitWithErrorAndHelp(optionName, dataPath, "does not exist as a writable directory");
			throw new ShouldHaveExitedException();
		}
		return dataPath;
	}

	@NotNull
	public final File readableFile(@NotNull final OptionSet optionSet, @NonNls @NotNull final String optionName)
	{
		final File dataPath = (File) optionSet.valueOf(optionName);
		if (!(dataPath.exists() && dataPath.canRead() && dataPath.isFile()))
		{
			exitWithErrorAndHelp(optionName, dataPath, "does not exist as a readable file");
			throw new ShouldHaveExitedException();
		}
		return dataPath;
	}

	@SuppressWarnings({"SimplifiableIfStatement", "BooleanMethodNameMustStartWithQuestion"})
	public final boolean booleanOption(@NotNull final OptionSet optionSet, @NotNull @NonNls final String optionName)
	{
		if (optionSet.has(optionName))
		{
			if (optionSet.hasArgument(optionName))
			{
				return (boolean) optionSet.valueOf(optionName);
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings({"ProhibitedExceptionDeclared", "FinalizeDeclaration"})
	@Override
	protected final void finalize() throws Throwable
	{
		super.finalize();
	}

	@SuppressWarnings("CloneInNonCloneableClass")
	@Override
	protected final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	private void execute(@NotNull final String... commandLineArguments)
	{
		final OptionSet optionSet;
		try
		{
			optionSet = options.parse(commandLineArguments);
		}
		catch (OptionException e)
		{
			exitWithErrorAndHelp(e.getMessage());
			throw new ShouldHaveExitedException(e);
		}

		if (optionSet.has(HelpOption))
		{
			exitWithHelp();
			throw new ShouldHaveExitedException();
		}

		if (optionSet.has(VersionOption))
		{
			exitWithVersion();
			throw new ShouldHaveExitedException();
		}

		if (doesNotHaveNonOptionArguments && !optionSet.nonOptionArguments().isEmpty())
		{
			exitWithErrorAndHelp("Non-option arguments are not understood");
			throw new ShouldHaveExitedException();
		}

		try
		{
			execute(optionSet);
		}
		catch (Exception e)
		{
			exitWithException(e);
			throw new ShouldHaveExitedException(e);
		}
	}

	private void exitWithHelp()
	{
		try
		{
			options.printHelpOn(outputStream);
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Failed to write help", e);
		}
		Help.exit();
	}

	private void exitWithVersion()
	{
		final String programVersion = "2013.02.X";
		outputStream.println(format(ENGLISH, "%1$s %2$s", programName(), programVersion));
		outputStream.println("© Crown Copyright 2013");
		outputStream.println();
		outputStream.println("Licensed under the Apache License, Version 2.0 (the \"License\");");
		outputStream.println("you may not use this file except in compliance with the License.");
		outputStream.println("You may obtain a copy of the License at");
		outputStream.println();
		outputStream.println("    http://www.apache.org/licenses/LICENSE-2.0");
		outputStream.println();
		outputStream.println("Unless required by applicable law or agreed to in writing, software");
		outputStream.println("distributed under the License is distributed on an \"AS IS\" BASIS,");
		outputStream.println("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
		outputStream.println("See the License for the specific language governing permissions and");
		outputStream.println("limitations under the License.");
		outputStream.println();
		outputStream.println("Written by Raphael Cohn (raphael.cohn@stormmq.com)");
		outputStream.println();
		Ok.exit();
	}

	@NotNull @NonNls
	private String programName()
	{
		final Class<? extends AbstractConsoleEntryPoint> clazz = getClass();
		final String simpleName = clazz.getSimpleName().replace(ConsoleEntryPoint, "");

		final String[] words = CamelCaseSplit.split(simpleName);
		final StringWriter writer = new StringWriter(simpleName.length() * 2);
		boolean afterFirst = false;
		for (final String word : words)
		{
			if (afterFirst)
			{
				writer.write((int) Hyphen);
			}
			else
			{
				afterFirst = true;
			}
			writer.write(word.toLowerCase(ENGLISH));
		}
		return writer.toString();
	}

	private void exitWithErrorAndHelp(@NonNls @NotNull final String message)
	{
		errorStream.println(message);
		try
		{
			options.printHelpOn(outputStream);
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Failed to write error message and help", e);
		}
		GeneralError.exit();
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private void exitWithException(@NotNull final Exception e)
	{
		e.printStackTrace(errorStream);
		GeneralError.exit();
	}
}
