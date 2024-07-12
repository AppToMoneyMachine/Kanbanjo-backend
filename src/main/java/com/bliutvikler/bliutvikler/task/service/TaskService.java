package com.bliutvikler.bliutvikler.task.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.swimlane.repository.SwimlaneRepository;
import com.bliutvikler.bliutvikler.task.model.Task;
import com.bliutvikler.bliutvikler.task.repository.TaskRepository;
import com.bliutvikler.bliutvikler.user.model.User;
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

    public Task createTask(Task task, Long boardId, User currentUser) {
        // Finne tilhørende board og sjekk om bord finnes
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (!boardOptional.isPresent()) {
            throw new IllegalStateException("Board not found");
        }
        Board board = boardOptional.get();

        // check if user is owner or participant of the board
        boolean isOwner = board.getOwner().getId().equals(currentUser.getId());
        boolean isParticipant = board.getParticipants().stream().anyMatch(participant -> participant.getId().equals(currentUser.getId()));

        if (!isOwner && !isParticipant) {
            throw new IllegalStateException("User is not authorized to create tasks in this board");
        }

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

    public Task moveTaskBetweenSwimlanes(Long taskId, Long targetSwimlaneId, User currentUser) {
        // finne tasken som skal flyttes
        Task taskToBeMoved = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        // ønsket destinasjon - id på swimlane
        Swimlane targetSwimlane = swimlaneRepository.findById(targetSwimlaneId).orElseThrow(() -> new IllegalArgumentException("Swimlane not found with ID: " + targetSwimlaneId));

        // get board
        Board board = taskToBeMoved.getSwimlane().getBoard();

        // check if user is owner or participant of the board
        boolean isOwner = board.getOwner().getId().equals(currentUser.getId());
        boolean isParticipant = board.getParticipants().stream().anyMatch(participant -> participant.getId().equals(currentUser.getId()));


        if (!isOwner && !isParticipant) {
            throw new IllegalStateException("User is not authorized to move tasks in this board");
        }

        if (!taskToBeMoved.getSwimlane().getBoard().getId().equals(targetSwimlane.getBoard().getId())) {
            throw new IllegalArgumentException("Swimlane is not on the same board as the task");
        }
        taskToBeMoved.setSwimlane(targetSwimlane);
        return taskRepository.save(taskToBeMoved);
    }

    public Task updateTask(Long taskId, Task taskInputData, User currentUser) {

        // find task to be updated
        Task taskToBoard = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found with ID:" + taskId));
        // find board
        Board board = taskToBoard.getSwimlane().getBoard();

        // check if user is owner or participant of the board
        boolean isOwner = board.getOwner().getId().equals(currentUser.getId());
        boolean isParticipant = board.getParticipants().stream().anyMatch(participant -> participant.getId().equals(currentUser.getId()));

        if (!isOwner && !isParticipant) {
            throw new IllegalStateException("User is not authorized to update tasks in this board");
        }

        // finne tasken som skal oppdateres
        Task taskToBeUpdated = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found with ID:" + taskId));
        // data som task skal oppdateres med
        if (taskInputData.getName() != null) {
            taskToBeUpdated.setName(taskInputData.getName());
        }
        if (taskInputData.getDescription() != null) {
            taskToBeUpdated.setDescription(taskInputData.getDescription());
        }
        if (taskInputData.getParticipant() != null) {
            taskToBeUpdated.setParticipant(taskInputData.getParticipant());
        }
        if (taskInputData.getStatus() != null) {
            taskToBeUpdated.setStatus(taskInputData.getStatus());
        }
        if (taskInputData.getPriority() != null) {
            taskToBeUpdated.setPriority(taskInputData.getPriority());
        }
        return taskRepository.save(taskToBeUpdated);
    }

    public void deleteTask(Long taskId, User currentUser) {
        // finne tasken som skal slettes
        Task taskToBeDeleted = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        // find board
        Board board = taskToBeDeleted.getSwimlane().getBoard();

        // check if user is owner or participant of the board
        boolean isOwner = board.getOwner().getId().equals(currentUser.getId());
        boolean isParticipant = board.getParticipants().stream().anyMatch(participant -> participant.getId().equals(currentUser.getId()));

        if (!isOwner && !isParticipant) {
            throw new IllegalStateException("User is not authorized to delete tasks in this board");
        }

        taskRepository.delete(taskToBeDeleted);
    }
}
