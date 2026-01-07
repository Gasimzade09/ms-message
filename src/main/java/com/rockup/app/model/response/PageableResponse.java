package com.rockup.app.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableResponse<T> {

    private List<T> data;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;

    public static <T> PageableResponse<T> of(Page<T> page) {
        return new PageableResponse<>(page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }
}
