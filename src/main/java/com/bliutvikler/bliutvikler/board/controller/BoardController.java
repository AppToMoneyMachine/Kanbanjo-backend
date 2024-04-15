package com.bliutvikler.bliutvikler.board.controller;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        Board savedBoard = boardService.saveBoard(board);
        return ResponseEntity.ok(savedBoard);
    }

    @GetMapping("{id}")
    public ResponseEntity<Board> getBoard(@PathVariable Long id) {
        return boardService.getBoard(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
