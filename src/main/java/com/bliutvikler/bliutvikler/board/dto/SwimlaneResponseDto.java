package com.bliutvikler.bliutvikler.board.dto;

import com.bliutvikler.bliutvikler.task.dto.TaskResponseDto;

import java.util.List;

public record SwimlaneResponseDto(
        Long id,
        String name,
        List<TaskResponseDto> tasks
) {
}
