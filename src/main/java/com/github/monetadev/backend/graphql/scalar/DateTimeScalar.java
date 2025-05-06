package com.github.monetadev.backend.graphql.scalar;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;


/**
 * Utilizes {@link ExtendedScalars} implementation to map {@link java.time.OffsetDateTime} objects.
 * @author MonetaDev
 */
@DgsComponent
public class DateTimeScalar {

    @DgsRuntimeWiring
    public RuntimeWiring.Builder addScalars(RuntimeWiring.Builder builder) {
        return builder.scalar(ExtendedScalars.DateTime);
    }
}