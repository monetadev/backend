package com.github.monetadev.backend.graphql.type.pagination;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PaginatedResponse<T> {
    private List<T> items;
    private int totalPages;
    private long totalElements;
    private int currentPage;

    public PageInfo getPageInfo() {
        return new PageInfo(totalPages, totalElements, currentPage);
    }

    public static abstract class PaginatedResponseBuilder<T, C extends PaginatedResponse<T>, B extends PaginatedResponseBuilder<T, C, B>> {
        protected List<T> items;
        protected int totalPages;
        protected long totalElements;
        protected int currentPage;

        protected abstract B self();

        public B items(List<T> items) {
            this.items = items;
            return self();
        }

        public B totalPages(int totalPages) {
            this.totalPages = totalPages;
            return self();
        }

        public B totalElements(long totalElements) {
            this.totalElements = totalElements;
            return self();
        }

        public B currentPage(int currentPage) {
            this.currentPage = currentPage;
            return self();
        }

        public abstract C build();
    }
}
