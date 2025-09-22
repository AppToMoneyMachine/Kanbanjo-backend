package com.bliutvikler.bliutvikler.task.controller;

import com.bliutvikler.bliutvikler.task.model.Task;
import com.bliutvikler.bliutvikler.task.service.TaskService;
import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/task") // Base URL for alle ruter i denne kontrolleren
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    // getTask

    @PostMapping("/create/{boardId}")
    public ResponseEntity<Task> createTask(@RequestBody Task task, @PathVariable Long boardId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            Task savedTask = taskService.createTask(task, boardId, currentUser);
            logger.info("Task created successfully with ID {}", savedTask.getId(), savedTask);
            return ResponseEntity.ok(savedTask);
        } catch (IllegalStateException e) {
            logger.error("Error creating task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch(Exception e) {
            logger.error("Error creating task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @PutMapping("/{taskId}/move/{swimlaneId}")
    public ResponseEntity<Task> moveTask(@PathVariable Long taskId, @PathVariable Long swimlaneId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            Task movedTask = taskService.moveTaskBetweenSwimlanes(taskId, swimlaneId, currentUser);
            logger.info("Task moved successfully with ID {}", taskId);
            return ResponseEntity.ok(movedTask);
        } catch (IllegalStateException e) {
            logger.error("Error moving task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch(Exception e) {
            logger.error("Error moving task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<Task> updateTask(@RequestBody Task task ,@PathVariable Long taskId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            Task updatedTask = taskService.updateTask(taskId, task, currentUser);
            logger.info("Task updated successfully with ID {}", taskId);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalStateException e) {
            logger.error("Error updating task - bad request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Errorupdating task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            taskService.deleteTask(taskId, currentUser);
            logger.info("Task deleted successfully with ID {}", taskId);
            return ResponseEntity.ok().build(); // ingen body returneres pga delete
        } catch (IllegalArgumentException e) {
            logger.error("Task to be deleted was not found {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error deleting task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
