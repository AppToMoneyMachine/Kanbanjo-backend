package com.bliutvikler.bliutvikler.board.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Board createBoardWithDefaultSwimlanes(Board board) {
        List<String> defaultSwimlaneNames = Arrays.asList("todo", "in_progress", "review", "done");
        defaultSwimlaneNames.forEach(name -> {
            Swimlane swimlane = new Swimlane();
            swimlane.setName(name);
            board.addSwimlane(swimlane);
        });

        return boardRepository.save(board);
    }

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    public Optional<Board> getBoard(Long id) {
        return boardRepository.findById(id);
    }
}
