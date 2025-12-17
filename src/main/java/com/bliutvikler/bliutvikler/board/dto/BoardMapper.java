package com.bliutvikler.bliutvikler.board.dto;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.task.dto.TaskMapper;
import com.bliutvikler.bliutvikler.task.dto.TaskResponseDto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoardMapper {
    private BoardMapper() {}

    public static BoardResponseDto toDto(Board board) {
        List<SwimlaneResponseDto> swimlanes = board.getSwimlanes() == null
                ? Collections.emptyList()
                : board.getSwimlanes().stream()
                .sorted(Comparator.comparing(swimlane -> swimlane.getId()))
                .map(sw -> new SwimlaneResponseDto(
                        sw.getId(),
                        sw.getName(),
                        sw.getTasks() == null
                            ? Collections.<TaskResponseDto>emptyList()
                                : sw.getTasks().stream()
                                .map(task -> TaskMapper.toDto(task))
                                .toList()
                ))
                .toList();

        return new BoardResponseDto(
                board.getId(),
                board.getName(),
                board.getTaskCount(),
                swimlanes
        );
    }
}
