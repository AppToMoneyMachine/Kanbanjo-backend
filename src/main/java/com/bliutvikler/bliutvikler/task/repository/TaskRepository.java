package com.bliutvikler.bliutvikler.task.repository;

import com.bliutvikler.bliutvikler.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Metoder for å hente oppgaver, som å finne oppgaver etter status eller prioritet.
}
