package com.bliutvikler.bliutvikler.task.dto;

import java.time.LocalDateTime;

public record TaskResponseDto(
        Long id,
        String name,
        String description,
        String status,
        String priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long swimlaneId,
        Long boardId,
        Long participantId
) {}
