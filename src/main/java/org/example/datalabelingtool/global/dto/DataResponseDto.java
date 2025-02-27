package org.example.datalabelingtool.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DataResponseDto<T> {
    private T data;

    @Builder
    public DataResponseDto(T data) {
        this.data = data;
    }
}
