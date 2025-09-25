package com.bliutvikler.bliutvikler.board.controller;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.service.BoardService;
import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("api/board")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated") // only logged in users can create a board
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        try {
            // set the current user as the owner of the board
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database
            board.setOwner(currentUser);

            Board savedBoard = boardService.createBoardWithDefaultSwimlanes(board);
            logger.info("Board created successfully with ID {}", savedBoard.getId());
            return ResponseEntity.ok(savedBoard);
        } catch(Exception e) {
            logger.error("Error creating board: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("isAuthenticated") // only logged in users can read a board
    @GetMapping("{id}")
    public ResponseEntity<Board> getBoard(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            return boardService.getBoard(id, currentUser)
                    .map(board -> {
                        logger.info("Fetching board with ID {}", id);
                        return ResponseEntity.ok(board);
                    })
                    .orElseGet(() -> {
                        logger.info("Board not found with ID {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error retrieving board with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("isAuthenticated") // only logged in users can read personal boards
    @GetMapping("mine")
    public ResponseEntity<List<Board>> getBoardByOwner() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            List<Board> boards = boardService.getBoardByOwnerId(currentUser);
            logger.info("Boards found by service {}:", boards);

            if (boards.isEmpty()) {
                logger.info("No boards found on that owner id");
                return ResponseEntity.noContent().build();
            }
            logger.info("Fetched {} boards for user {}", boards.size(), username);
            return ResponseEntity.ok(boards);

        } catch (Exception e) {
            logger.error("Error retrieving boards for current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("isAuthenticated")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            boardService.deleteBoard(id, currentUser);
            logger.info("Task deleted successfully with ID {}", id);
            return ResponseEntity.ok().build(); // ingen body returneres pga delete
        } catch (IllegalArgumentException e) {
            logger.error("Board to be deleted was not found {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error deleting board: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("isAuthenticated")
    @PostMapping("/{boardId}/add-participant")
    public ResponseEntity<Void> addParticipantToBoard(@PathVariable Long boardId, @RequestParam String participantUsername) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String ownerUsername = authentication.getName();
            User owner = userService.findByUsername(ownerUsername);

            boardService.addParticipantToBoard(boardId, participantUsername, owner);
            logger.info("Participant added successfully with username " + participantUsername);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error adding participant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error adding participant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("isAuthenticated")
    @DeleteMapping("{boardId}/remove-participant")
    public ResponseEntity<Void> deleteParticipantFromBoard(@PathVariable Long boardId, @RequestParam String participantUsername) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String ownerUsername = authentication.getName();
            User owner = userService.findByUsername(ownerUsername);

            boardService.deleteParticipantFromBoard(boardId, participantUsername, owner);
            logger.info("Participant deleted successfully with username " + participantUsername);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting participant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error deleting participant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
