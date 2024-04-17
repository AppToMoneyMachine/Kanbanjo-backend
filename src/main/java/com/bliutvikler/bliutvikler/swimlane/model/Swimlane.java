package com.bliutvikler.bliutvikler.swimlane.model;

import com.bliutvikler.bliutvikler.board.model.Board;
import com.bliutvikler.bliutvikler.task.model.Task;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Swimlane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "board_id")  // Denne linjen spesifiserer navnet på kolonnen som inneholder fremmednøkkelen.
    @JsonBackReference
    private Board board;  // Dette er referansen tilbake til Board.

    // fremmednøkkel ligger i Task tabellen.
    // Cascade gir mye automatiser funksjon knyttet til relasjonen mellom Task og swimlane
    @OneToMany(mappedBy = "swimlane", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Task> tasks;

    public Swimlane() {
        // Nødvendig tom konstruktør for JPA
    }

    public Swimlane(String name, List<Task> tasks, Board board) {
        this.name = name;
        this.tasks = tasks;
        this.board = board;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Board getBoard() {
        return board;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setTasks (List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}

