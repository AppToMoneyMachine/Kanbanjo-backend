package com.bliutvikler.bliutvikler.swimlane.repository;

import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SwimlaneRepository extends JpaRepository<Swimlane, Long> {
    // Metoder for å administrere swimlanes
}