package com.github.monetadev.backend.graphql.type;

import com.github.monetadev.backend.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthPayload {
    private String token;
    private User user;
}
