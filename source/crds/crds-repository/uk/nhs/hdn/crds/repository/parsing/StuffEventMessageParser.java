package uk.nhs.hdn.crds.repository.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.SingleLineCommaSeparatedValueParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.lineParsers.LineParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.linesParsers.LinesParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.ToDomainSeparatedValueParseEventHandler;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.StuffEventKind;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffEventIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;
import uk.nhs.hdn.number.NhsNumber;

import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyLongFieldParser.NonEmptyLongFieldParserInstance;
import static uk.nhs.hdn.crds.registry.domain.metadata.parsing.IdentifierFieldParser.*;
import static uk.nhs.hdn.crds.repository.parsing.NhsNumberFieldParser.NhsNumberParserInstance;
import static uk.nhs.hdn.crds.repository.parsing.StuffEventKindFieldParser.StuffEventKindParserInstance;

public final class StuffEventMessageParser extends AbstractToString implements LinesParser<StuffEventMessage, StuffEventMessageUser>, LineParser<StuffEventMessage, StuffEventMessageUser>
{
	@NotNull
	public static Parser singleLineStuffEventMessageParser(@NotNull final StuffEventMessageUser stuffEventMessageUser)
	{
		final StuffEventMessageParser stuffEventMessageParser = new StuffEventMessageParser(stuffEventMessageUser);
		return new SingleLineCommaSeparatedValueParser<>
		(
			new ToDomainSeparatedValueParseEventHandler<>
			(
				stuffEventMessageParser,
				stuffEventMessageParser,

				NhsNumberParserInstance,
				ProviderIdentifierFieldParserInstance,
				RepositoryIdentifierFieldParserInstance,
				StuffIdentifierFieldParserInstance,
				StuffEventIdentifierFieldParserInstance,
				NonEmptyLongFieldParserInstance,
				StuffEventKindParserInstance
			)
		);
	}

	@NotNull private final StuffEventMessageUser stuffEventMessageUser;

	public StuffEventMessageParser(@NotNull final StuffEventMessageUser stuffEventMessageUser)
	{
		this.stuffEventMessageUser = stuffEventMessageUser;
	}

	@NotNull
	@Override
	public StuffEventMessageUser newParsedLines()
	{
		return stuffEventMessageUser;
	}

	@Override
	public void parse(@MillisecondsSince1970 final long lastModified, @NotNull final StuffEventMessageUser parsedLines)
	{
	}

	@Override
	public void parse(final int lineIndex, @NotNull final Object[] parsedFields, @NotNull final StuffEventMessageUser parsedLines)
	{
		parsedLines.use
		(
			new StuffEventMessage
			(
				(NhsNumber) parsedFields[0],
				(ProviderIdentifier) parsedFields[1],
				(RepositoryIdentifier) parsedFields[2],
				(StuffIdentifier) parsedFields[3],
				new StuffEvent
				(
					(StuffEventIdentifier) parsedFields[4],
					(long) parsedFields[5],
					(StuffEventKind) parsedFields[6]
				)
			)
		);
	}

}
