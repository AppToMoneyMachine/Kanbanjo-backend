package com.bliutvikler.bliutvikler.task.dto;

import com.bliutvikler.bliutvikler.task.model.Task;

public class TaskMapper {
    private TaskMapper() {}

    public static TaskResponseDto toDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getSwimlane() != null ? task.getSwimlane().getId() : null,
                task.getBoard() != null ? task.getBoard().getId() : null,
                task.getParticipant() != null ? task.getParticipant().getId() : null
        );
    }
}
