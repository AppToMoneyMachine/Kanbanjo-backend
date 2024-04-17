package com.bliutvikler.bliutvikler.board.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


import com.bliutvikler.bliutvikler.participant.model.Participant;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.task.model.Task;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Swimlane> swimlanes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "board_participants",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<Participant> participants;

    public Board() {
        // Nødvendig tom konstruktør for JPA
    }

    public Board(Long id, String name, List<Swimlane> swimlanes, List<Task> tasks, List<Participant> participants) {
        this.name = name;
        this.swimlanes = swimlanes;
        this.tasks = tasks;
        this.participants = participants;
    }
    // getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTaskCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public List<Swimlane> getSwimlanes() {
        return swimlanes;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void addSwimlane(Swimlane swimlane) {
        swimlanes.add(swimlane);
        swimlane.setBoard(this);
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSwimlanes(List<Swimlane> swimlanes) {
        this.swimlanes = swimlanes;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

}
