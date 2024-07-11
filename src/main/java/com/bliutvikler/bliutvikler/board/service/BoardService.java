package com.bliutvikler.bliutvikler.board.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.user.model.User;
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

    public Optional<Board> getBoard(Long id, User currentUser) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + id));

        if(!board.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User is not the owner of the board");
        }

        return boardRepository.findById(id);
    }

    public void deleteBoard(Long boardId, User currentUser) {
        // find the board to be deleted
        Board boardToBeDeleted = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + boardId));

        if(!boardToBeDeleted.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User is not the owner of the board");
        }

        boardRepository.delete(boardToBeDeleted);
    }
}
