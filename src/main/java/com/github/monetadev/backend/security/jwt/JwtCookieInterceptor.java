package com.github.monetadev.backend.security.jwt;

import com.github.monetadev.backend.service.security.JwtService;
import graphql.GraphQLContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Component
public class JwtCookieInterceptor implements WebGraphQlInterceptor {
    private final JwtService jwtService;
    private final String cookieName;
    private final Duration cookieMaxAge;
    private final String cookiePath;
    private final Boolean isSecure;
    private final String sameSitePolicy;

    public JwtCookieInterceptor(JwtService jwtService,
                                @Autowired String cookieName,
                                @Autowired Duration cookieMaxAge,
                                @Autowired String cookiePath,
                                @Autowired Boolean isSecure,
                                @Autowired String sameSitePolicy) {
        this.jwtService = jwtService;
        this.cookieName = cookieName;
        this.cookieMaxAge = cookieMaxAge;
        this.cookiePath = cookiePath;
        this.isSecure = isSecure;
        this.sameSitePolicy = sameSitePolicy;
    }

    @NotNull
    @Override
    public Mono<WebGraphQlResponse> intercept(@NotNull WebGraphQlRequest request, Chain chain) {
        return chain.next(request).doOnNext(response -> {
            GraphQLContext context = response.getExecutionInput().getGraphQLContext();
            if (context == null) return;

            Object tokenObject = context.get(cookieName);
            if (!(tokenObject instanceof String token)) return;
            if (!jwtService.isValidToken(token)) return;

            try {
                String cookieValue = buildCookie(token);
                response.getResponseHeaders().add(HttpHeaders.SET_COOKIE, cookieValue);
            } catch (Exception ignored) {
                // We shouldn't disrupt the response flow.
                // TODO: Implement logging in rest of class.
            }
        });
    }

    private String buildCookie(String token) {
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(cookieName).append("=").append(token).append("; ");
        cookieBuilder.append("Path=").append(cookiePath).append("; ");
        cookieBuilder.append("Max-Age=").append(cookieMaxAge.toSeconds()).append("; ");
        cookieBuilder.append("HttpOnly; ");
        if (isSecure){
            cookieBuilder.append("Secure; ");
        }
        cookieBuilder.append("SameSite=").append(sameSitePolicy).append(";");
        return cookieBuilder.toString();
    }
}
