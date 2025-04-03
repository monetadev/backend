package com.github.monetadev.backend.graphql.scalar;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.Value;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@DgsComponent
public class UploadScalar {
    @DgsRuntimeWiring
    public RuntimeWiring.Builder addScalars(RuntimeWiring.Builder builder) {
        return builder.scalar(GraphQLScalarType.newScalar()
                .name("Upload")
                .description("Upload GraphQL Scalar for MultipartFile")
                .coercing(new Coercing<MultipartFile, Void>() {
                    @Override
                    public Void serialize(@NotNull Object dataFetcherResult,
                                          @NotNull GraphQLContext context,
                                          @NotNull Locale locale) {
                        throw new CoercingSerializeException("Cannot serialize Multipart object!");
                    }

                    @Override
                    public MultipartFile parseValue(@NotNull Object input,
                                                    @NotNull GraphQLContext context,
                                                    @NotNull Locale locale) {
                        if (input instanceof MultipartFile file) {
                            return file;
                        }
                        throw new CoercingParseValueException("Invalid input type for Multipart object!");
                    }

                    @Override
                    public MultipartFile parseLiteral(@NotNull Value<?> input,
                                                      @NotNull CoercedVariables variables,
                                                      @NotNull GraphQLContext context,
                                                      @NotNull Locale locale) {
                        throw new CoercingParseLiteralException("Cannot parse Literal for Multipart object!");
                    }
                })
                .build()
        );
    }

}
