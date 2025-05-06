package com.github.monetadev.backend.graphql.type.pagination;

import com.github.monetadev.backend.model.UserLogin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaginatedUserLogin extends PaginatedResponse<UserLogin> {
    private PaginatedUserLogin(List<UserLogin> userLogins, int totalPages, long totalElements, int currentPage)  {
        super(userLogins, totalPages, totalElements, currentPage);
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder extends PaginatedResponseBuilder<UserLogin, PaginatedUserLogin, Builder> {
        @Override
        protected Builder self() {
            return this;
        }
        @Override
        public PaginatedUserLogin build() {
            return new PaginatedUserLogin(items, totalPages, totalElements, currentPage);
        }
    }
}
