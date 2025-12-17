package com.bliutvikler.bliutvikler.board.dto;

import java.util.List;

public record BoardResponseDto(
        Long id,
        String name,
        int taskCount,
        List<SwimlaneResponseDto> swimlanes
) {
}
