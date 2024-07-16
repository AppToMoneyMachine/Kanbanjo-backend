package com.bliutvikler.bliutvikler.board.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import com.bliutvikler.bliutvikler.participant.model.Participant;
import com.bliutvikler.bliutvikler.participant.repository.ParticipantRepository;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ParticipantRepository participantRepository;

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

    public void addParticipantToBoard(Long boardId, String participantUsername, User owner) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + boardId));

        // check if the authenticated user is owner of the board
        if (!board.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("User is not the owner of this board - not permitted to add participant");
        }

        User participantUser = userService.findByUsername(participantUsername);
        if (participantUser == null) {
            throw new IllegalStateException("Participant not found with username: " + participantUsername);
        }

        // check if participant already exists in board
        Optional<Participant> existingParticipant = board.getParticipants().stream()
                .filter(p -> p.getUsername().equals(participantUsername))
                .findFirst();

        if (existingParticipant.isPresent()) {
            throw new IllegalArgumentException("Participant already exists in the board");
        }

        // convert user to participant
        Participant participant = new Participant();
        participant.setUsername(participantUser.getUsername());
        participant.setRole("participant");

        // Save participant before adding to board to avoid TransientObjectException
        participant = participantRepository.save(participant);

        board.getParticipants().add(participant);
        boardRepository.save(board);
    }

    public void deleteParticipantFromBoard(Long boardId, String participantUsername, User owner) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + boardId));

        // check if the authenticated user is owner of the board
        if (!board.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("User is not the owner of this board - not permitted to delete participants");
        }

        // find participant by username
        Participant participant = board.getParticipants().stream()
                .filter(p -> p.getUsername().equals(participantUsername))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Participant not found with username: " + participantUsername));

        // remove participant from board
        board.getParticipants().remove(participant);
        boardRepository.save(board);
    }
}
