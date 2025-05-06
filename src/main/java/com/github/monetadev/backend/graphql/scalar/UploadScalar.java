package com.github.monetadev.backend.graphql.scalar;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.Value;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import name.nkonev.multipart.spring.graphql.coercing.webmvc.UploadCoercing;
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
                .coercing(new UploadCoercing())
                .build());
    }
}
