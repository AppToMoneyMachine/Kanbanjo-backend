package com.bliutvikler.bliutvikler.participant.repository;

import com.bliutvikler.bliutvikler.participant.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    // Metoder for å søke etter deltakere basert på feks rolle
}
