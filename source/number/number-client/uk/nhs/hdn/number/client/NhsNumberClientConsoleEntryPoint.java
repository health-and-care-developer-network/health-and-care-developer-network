package uk.nhs.hdn.number.client;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.digits.Digit;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.number.NhsNumber;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.digits.Digits.digits;
import static uk.nhs.hdn.number.NhsNumberExtractingCheckDigitCalculator.NhsNumberExtractingCheckDigitCalculatorInstance;

public final class NhsNumberClientConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String ValidateOption = "validate";
	private static final String CreateCheckDigitOption = "create-check-digit";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(NhsNumberClientConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(ValidateOption, "Ronseal option 1").withRequiredArg().ofType(String.class).describedAs("10 digit NHS number to validate");
		options.accepts(CreateCheckDigitOption, "Ronseal option 2").withRequiredArg().ofType(String.class).describedAs("9 digits to generate check digit for");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet)
	{
		final boolean hasValidate = optionSet.has(ValidateOption);
		final boolean hasCreateCheckDigit = optionSet.has(CreateCheckDigitOption);

		if (hasValidate)
		{
			if (hasCreateCheckDigit)
			{
				exitWithErrorAndHelp("--%1$s and --%2$s are mutually incompatible", ValidateOption, CreateCheckDigitOption);
			}

			final String nhsNumberToValidate = required(optionSet, ValidateOption);
			validateNhsNumber(nhsNumberToValidate);
			return;
		}

		if (hasCreateCheckDigit)
		{
			final String nhsNumberToGenerateCheckDigitFor = required(optionSet, CreateCheckDigitOption);
			generateNhsNumberWithCheckDigit(nhsNumberToGenerateCheckDigitFor);
			return;
		}

		exitWithErrorAndHelp("Either --%1$s or --%2$s is required", ValidateOption, CreateCheckDigitOption);
	}

	private static void validateNhsNumber(@NotNull final String nhsNumberToValidate)
	{
		final NhsNumber valid;
		try
		{
			valid = NhsNumber.valueOf(nhsNumberToValidate);
		}
		catch(RuntimeException invalid)
		{
			throw new IllegalStateException(format(ENGLISH, "NHS Number %1$s (supposedly with check digit) was invalid because '%2$s'", nhsNumberToValidate, invalid.getMessage()), invalid);
		}
		printNhsNumber(valid);
	}

	private static void generateNhsNumberWithCheckDigit(@NotNull final CharSequence nhsNumberToGenerateCheckDigitFor)
	{
		final Digits digits;
		final Digit checkDigit;
		try
		{
			digits = digits(nhsNumberToGenerateCheckDigitFor);
			NhsNumberExtractingCheckDigitCalculatorInstance.guardCorrectNumberOfDigitsIfNoCheckDigit(digits);
			checkDigit = NhsNumberExtractingCheckDigitCalculatorInstance.calculateCheckDigit(digits);
		}
		catch (RuntimeException invalid)
		{
			throw new IllegalStateException(format(ENGLISH, "NHS Number %1$s (supposedly without check digit) was invalid because '%2$s'", nhsNumberToGenerateCheckDigitFor, invalid.getMessage()), invalid);
		}

		printNhsNumber(new NhsNumber(digits.add(checkDigit)));
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void printNhsNumber(final NhsNumber valid)
	{
		out.println(valid.toDigitsString());
	}
}
