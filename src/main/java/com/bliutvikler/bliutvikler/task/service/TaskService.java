package com.bliutvikler.bliutvikler.task.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.swimlane.repository.SwimlaneRepository;
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
    private SwimlaneRepository swimlaneRepository;

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
        // Legg til swimlane i task
        Swimlane todoSwimlane = swimlanes.get(0);
        task.setSwimlane(todoSwimlane);
        todoSwimlane.getTasks().add(task);

        return taskRepository.save(task);
    }

    public Task moveTaskBetweenSwimlanes(Long taskId, Long targetSwimlaneId) {
        // finne tasken som skal flyttes
        Task taskToBeMoved = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        // ønsket destinasjon - id på swimlane
        Swimlane targetSwimlane = swimlaneRepository.findById(targetSwimlaneId).orElseThrow(() -> new IllegalArgumentException("Swimlane not found with ID: " + targetSwimlaneId));

        if (!taskToBeMoved.getSwimlane().getBoard().getId().equals(targetSwimlane.getBoard().getId())) {
            throw new IllegalArgumentException("Swimlane is not on the same board as the task");
        }
        taskToBeMoved.setSwimlane(targetSwimlane);
        return taskRepository.save(taskToBeMoved);
    }

    public void deleteTask(Long taskId) {
        // finne tasken som skal slettes
        Task taskToBeDeleted = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        taskRepository.delete(taskToBeDeleted);
    }
}
