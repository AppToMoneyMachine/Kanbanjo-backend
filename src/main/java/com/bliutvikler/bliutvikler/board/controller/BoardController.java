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
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error adding participant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error adding participant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
