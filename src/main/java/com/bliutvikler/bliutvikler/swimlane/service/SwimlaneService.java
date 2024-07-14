package com.bliutvikler.bliutvikler.swimlane.service;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.board.repository.BoardRepository;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.swimlane.repository.SwimlaneRepository;
import com.bliutvikler.bliutvikler.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SwimlaneService {

    @Autowired
    private SwimlaneRepository swimlaneRepository;

    @Autowired
    private BoardRepository boardRepository;

    private static final int MAX_SWIMLANES = 10;

    public Swimlane createNewSwimlane(Long boardId, User currentUser) {
        // Finne tilhørende board og sjekk om bord finnes
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (!boardOptional.isPresent()) {
            throw new IllegalStateException("Board not found");
        }
        Board board = boardOptional.get();

        Boolean isOwner = board.getOwner().getId().equals(currentUser.getId());
        Boolean isParticipant = board.getParticipants().stream().anyMatch(participant -> participant.getId().equals(currentUser.getId()));

        if (!isOwner && !isParticipant) {
            throw new IllegalStateException("User is not authorized to create swimlanes in this board");
        }

        // Finne tilhørende swimlane
        List<Swimlane> swimlanes = board.getSwimlanes();

        if (swimlanes.isEmpty()) {
            throw new IllegalStateException("No swimlanes available on this board!");
        }

        Swimlane newDefaultSwimlane = new Swimlane("New swimlane", new ArrayList<>(), board);

        if (swimlanes.size() >= MAX_SWIMLANES) {
            throw new IllegalStateException("Max number of swimlanes reached.");
        }
        swimlanes.add(newDefaultSwimlane);
        board.setSwimlanes(swimlanes);

        return swimlaneRepository.save(newDefaultSwimlane);
    }

    public void deleteExistingSwimlane(Long swimlaneId, Long boardId, User currentUser) {
        Swimlane swimlaneToDelete = swimlaneRepository.findById(swimlaneId).orElseThrow(() -> new IllegalArgumentException("Swimlane not found with ID: " + swimlaneId));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Cannot find board with ID: " + boardId));
        List<Swimlane> swimlanes = board.getSwimlanes();

        Boolean isOwner = board.getOwner().getId().equals(currentUser.getId());
        Boolean isParticipant = board.getParticipants().stream().anyMatch(participant -> participant.getId().equals(currentUser.getId()));

        if (!isOwner && !isParticipant) {
            throw new IllegalStateException("User is not authorized to delete swimlanes in this board");
        }

        if (swimlanes.size() < 3) {
            throw new IllegalStateException("Too few swimlanes to use delete operation. Minimum 2 swimlanes must be present.");
        }

        swimlanes.remove(swimlaneToDelete);
        board.setSwimlanes(swimlanes);
        boardRepository.save(board);

        swimlaneRepository.delete(swimlaneToDelete);
    }

    public Swimlane updateSwimlaneName(Long swimlaneId, Swimlane swimLaneInputData, User currentUser) {
        Swimlane swimlaneToUpdate = swimlaneRepository.findById(swimlaneId).orElseThrow(() -> new IllegalArgumentException("Swimlane not found with ID: " + swimlaneId));

        Board board = swimlaneToUpdate.getBoard();

        Boolean isOwner = board.getOwner().getId().equals(currentUser.getId());
        Boolean isParticipant = board.getParticipants().stream().anyMatch(participant -> participant.getId().equals(currentUser.getId()));

        if (!isOwner && !isParticipant) {
            throw new IllegalStateException("User is not authorized to update swimlanes in this board");
        }

        swimlaneToUpdate.setName(swimLaneInputData.getName());
        return swimlaneRepository.save(swimlaneToUpdate);
    }
}
