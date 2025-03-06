package com.github.monetadev.backend.dgs.types;

import com.netflix.graphql.dgs.DgsScalar;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@DgsScalar(name = "UUID")
public class UUIDScalar implements Coercing<UUID, String> {
    @Override
    public String serialize(@NotNull Object dataFetcherResult) throws CoercingSerializeException {
        if (dataFetcherResult instanceof UUID) {
            return dataFetcherResult.toString();
        }
        throw new CoercingSerializeException("Expected UUID but was " + dataFetcherResult.getClass().getName());
    }

    @Override
    public UUID parseValue(@NotNull Object input) throws CoercingParseValueException {
        try {
            return UUID.fromString(input.toString());
        } catch (Exception e) {
            throw new CoercingParseValueException("Expected valid UUID but was " + input);
        }
    }

    @Override
    public UUID parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue s) {
            try {
                return UUID.fromString(s.getValue());
            } catch (Exception e) {
                throw new CoercingParseLiteralException("Expected valid UUID but was " + input);
            }
        }
        throw new CoercingParseLiteralException("Expected StringValue but was " + input.getClass().getName());
    }
}