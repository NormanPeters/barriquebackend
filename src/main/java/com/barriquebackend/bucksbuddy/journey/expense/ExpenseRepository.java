package com.barriquebackend.bucksbuddy.journey.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByExpenseId(Long id);

    // Query expenses by the journey's id and the journey's user id.
    List<Expense> findAllByJourney_JourneyIdAndJourney_User_Id(Long journeyId, Long userId);
}