package com.bliutvikler.bliutvikler.task.dto;

public record TaskCreateRequestDto(
        String name,
        String description,
        Long participant
) {}
