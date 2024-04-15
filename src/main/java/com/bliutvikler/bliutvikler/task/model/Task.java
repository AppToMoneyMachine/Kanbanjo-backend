package com.bliutvikler.bliutvikler.task.model;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.participant.model.Participant;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "participant_id") // Definerer kolonnenavn for fremmednøkkel i databasen
    private Participant participant;

    private String status;
    private String priority;

    @Column(name = "created_at")
    private LocalDateTime  createdAt;

    @Column(name = "updated_at")
    private LocalDateTime  updatedAt;

    @ManyToOne
    @JoinColumn(name = "swimlane_id") // Definerer kolonnenavn for fremmednøkkel i databasen
    private Swimlane swimlane;

    @ManyToOne
    @JoinColumn(name = "board_id") // Definerer kolonnenavn for fremmednøkkel i databasen
    private Board board;

    public Task() {
        // Nødvendig tom konstruktør for JPA
    }

    public Task(String name, String description, Participant participant, String status, String priority, LocalDateTime  createdAt, LocalDateTime  updatedAt, Swimlane swimlane, Board board) {
        this.name = name;
        this.description = description;
        this.participant = participant;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.swimlane = swimlane;
        this.board = board;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Participant getParticipant() {
        return participant;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDateTime  getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime  getUpdatedAt() {
        return updatedAt;
    }

    public Swimlane getSwimlane() {
        return swimlane;
    }

    public Board getBoard() {
        return board;
    }


    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCreatedAt(LocalDateTime  createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime  updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSwimlane(Swimlane swimlane) {
        this.swimlane = swimlane;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
