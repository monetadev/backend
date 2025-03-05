package com.github.monetadev.backend.dgs.types;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Custom GraphQL scalar type for handling ISO-8601 DateTime values.
 * This implementation supports {@link ZonedDateTime}, {@link Instant}, and {@link LocalDateTime} types.
 * All datetime values are serialized to ISO-8601 format with UTC timezone.
 *
 * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Standard</a>
 * @see <a href="https://www.graphql-java.com/documentation/scalars">GraphQL Java Scalars</a>
 *
 * @author MonetaDev
 */
@DgsComponent
public class DateTimeScalar {
    /**
     * The GraphQL scalar type definition for DateTime values.
     * This scalar handles parsing of ISO-8601 formatted strings and serialization of temporal objects.
     */
    public static final GraphQLScalarType DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("DateTime ISO8601 Scalar.")
            .coercing(new Coercing<TemporalAccessor, String>() {
                @Override
                public String serialize(@NotNull Object dataFetcherResult, @NotNull GraphQLContext context, @NotNull Locale locale) {
                    return serializeDateTime(dataFetcherResult);
                }

                @Override
                public TemporalAccessor parseValue(@NotNull Object input, @NotNull GraphQLContext context, @NotNull Locale locale) {
                    return parseDateTimeFromVariable(input);
                }

                @Override
                public TemporalAccessor parseLiteral(@NotNull Value<?> input, @NotNull CoercedVariables variables, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) {
                    return parseDateTimeFromAstLiteral(input);
                }
            }).build();

    /**
     * Serializes a temporal object to its ISO-8601 string representation.
     *
     * @param dataFetcherResult The temporal object to serialize
     * @return ISO-8601 formatted string in UTC timezone
     * @throws CoercingSerializeException if the input is null or cannot be serialized to a valid ISO-8601 string
     */
    private static String serializeDateTime(Object dataFetcherResult) {
        if (dataFetcherResult == null) {
            throw new CoercingSerializeException("Cannot serialize null value");
        }

        if (!(dataFetcherResult instanceof TemporalAccessor)) {
            throw new CoercingSerializeException("Object is not TemporalAccessor");
        }

        return switch (dataFetcherResult) {
            case ZonedDateTime zdt -> zdt.format(DateTimeFormatter.ISO_INSTANT);
            case Instant i -> i.toString();
            case LocalDateTime ldt -> ldt.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
            default ->
                    throw new CoercingSerializeException("Unable to parse valid TemporalAccessor object. How did we get here?");
        };
    }

    @DgsRuntimeWiring
    public RuntimeWiring.Builder runtimeWiring(RuntimeWiring.Builder builder) {
        return builder.scalar(DATE_TIME);
    }


    /**
     * Parses a DateTime value from a GraphQL variable.
     *
     * @param input The input object to parse
     * @return A {@link TemporalAccessor} representing the parsed datetime
     * @throws CoercingParseValueException if the input is not a string or cannot be parsed as a valid ISO-8601 datetime
     */
    private static TemporalAccessor parseDateTimeFromVariable(Object input) {
        if (!(input instanceof String parseCandidate)) {
            throw new CoercingParseValueException("DateTime must be a string value.");
        }
        return parseDateTime(parseCandidate);
    }

    /**
     * Parses a DateTime value from a GraphQL AST literal.
     *
     * @param input The AST input value to parse
     * @return A {@link TemporalAccessor} representing the parsed datetime
     * @throws CoercingParseLiteralException if the input is not a string value or cannot be parsed as a valid ISO-8601 datetime
     */
    private static TemporalAccessor parseDateTimeFromAstLiteral(Value<?> input) {
        if (!(input instanceof StringValue parseCandidate)) {
            throw new CoercingParseLiteralException("DateTime must be a string value");
        }
        return parseDateTime(parseCandidate.getValue());
    }

    /**
     * Attempts to parse a string into a temporal object.
     * First attempts to parse as {@link ZonedDateTime}, then falls back to {@link Instant}.
     *
     * @param input The ISO-8601 formatted string to parse
     * @return A {@link TemporalAccessor} representing the parsed datetime
     * @throws CoercingParseValueException if the input cannot be parsed as either a ZonedDateTime or Instant
     */
    private static TemporalAccessor parseDateTime(String input) {
        try {
            return ZonedDateTime.parse(input);
        } catch (DateTimeParseException e) {
            try {
                return Instant.parse(input);
            } catch (DateTimeParseException e2) {
                throw new CoercingParseValueException("Invalid ISO-8601 DateTime format. Expected format: YYYY-MM-DDThh:mm:ss.sssZ");
            }
        }
    }
}