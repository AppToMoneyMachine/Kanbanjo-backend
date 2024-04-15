    package com.bliutvikler.bliutvikler.participant.model;

    import com.bliutvikler.bliutvikler.board.model.Board;
    import jakarta.persistence.*;

    import java.util.List;

    @Entity
    public class Participant {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String firstName;
        private String lastName;
        private String role;

        @ManyToMany(mappedBy = "participants")
        private List<Board> boards;

        public Participant() {
            // Nødvendig tom konstruktør for JPA
        }

        public Participant(String firstName, String lastName, String role, List<Board> boards) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.boards = boards;
        }

        // getters
        public Long getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getRole() {
            return role;
        }

        public List<Board> getBoards() {
            return boards;
        }

        // setters
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setBoards(List<Board> boards) {
            this.boards = boards;
        }
    }
