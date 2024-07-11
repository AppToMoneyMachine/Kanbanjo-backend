package com.bliutvikler.bliutvikler.board.controller;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/board")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    @PreAuthorize("isAuthenticated") // only logged in users can create a board
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        try {
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
            return boardService.getBoard(id)
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
            boardService.deleteBoard(id);
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
}
