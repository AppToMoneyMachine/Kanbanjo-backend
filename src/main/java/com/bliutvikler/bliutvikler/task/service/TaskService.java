package com.bliutvikler.bliutvikler.task.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.task.model.Task;
import com.bliutvikler.bliutvikler.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task, Long boardId) {
        // Finne tilhørende board og sjekk om bord finnes
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (!boardOptional.isPresent()) {
            throw new IllegalStateException("Board not found");
        }
        Board board = boardOptional.get();
        // Finne tilhørende swimlane
        List<Swimlane> swimlanes = board.getSwimlanes();

        if (swimlanes.isEmpty()) {
            throw new IllegalStateException("No swimlanes available on this board!");
        }

        // Legge task under første swimlane
        Swimlane todoSwimlane = swimlanes.get(0);
        task.setSwimlane(todoSwimlane);
        todoSwimlane.getTasks().add(task);

        return taskRepository.save(task);
    }
}
