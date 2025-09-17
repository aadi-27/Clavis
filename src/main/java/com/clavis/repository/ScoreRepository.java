package com.clavis.repository;

import com.clavis.model.Score;
import com.clavis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findTop10ByOrderByPointsDesc();
    List<Score> findAllByUserOrderByPointsDesc(User user);
}
