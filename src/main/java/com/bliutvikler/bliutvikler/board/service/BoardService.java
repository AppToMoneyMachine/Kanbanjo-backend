package com.bliutvikler.bliutvikler.board.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    public Optional<Board> getBoard(Long id) {
        return boardRepository.findById(id);
    }
}
