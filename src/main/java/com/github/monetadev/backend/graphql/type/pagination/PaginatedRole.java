package com.github.monetadev.backend.graphql.type.pagination;

import com.github.monetadev.backend.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaginatedRole extends PaginatedResponse<Role> {
    private PaginatedRole(List<Role> items, int totalPages, long totalElements, int currentPage) {
        super(items, totalPages, totalElements, currentPage);
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder extends PaginatedResponseBuilder<Role, PaginatedRole, Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public PaginatedRole build() {
            return new PaginatedRole(items, totalPages, totalElements, currentPage);
        }
    }
}
