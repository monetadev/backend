package com.github.monetadev.backend.graphql.type.pagination;

import com.github.monetadev.backend.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaginatedUser extends PaginatedResponse<User> {
    private PaginatedUser(List<User> items, int totalPages, long totalElements, int currentPage) {
        super(items, totalPages, totalElements, currentPage);
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder extends PaginatedResponseBuilder<User, PaginatedUser, Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public PaginatedUser build() {
            return new PaginatedUser(items, totalPages, totalElements, currentPage);
        }
    }
}
