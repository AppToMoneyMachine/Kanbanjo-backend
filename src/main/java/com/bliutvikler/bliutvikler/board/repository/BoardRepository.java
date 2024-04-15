package com.bliutvikler.bliutvikler.board.repository;
import com.bliutvikler.bliutvikler.board.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // Her kan du legge til spesifikke metoder for å hente boards, f.eks. etter participant
}
