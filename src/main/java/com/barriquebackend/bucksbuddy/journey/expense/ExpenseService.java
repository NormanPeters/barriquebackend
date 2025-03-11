package com.barriquebackend.bucksbuddy.journey.expense;

import com.barriquebackend.bucksbuddy.journey.Journey;
import com.barriquebackend.bucksbuddy.journey.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling business logic related to expenseRepositorys.
 * Provides methods for creating, retrieving, updating, and deleting expenseRepositorys
 * that are associated with a specific journey and user.
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final JourneyRepository journeyRepository;

    /**
     * Constructs an expenseRepositoryService with the given repositories.
     *
     * @param expenseRepository the repository for performing CRUD operations on expenseRepositorys
     * @param journeyRepository     the repository for retrieving journeys
     */
    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, JourneyRepository journeyRepository) {
        this.expenseRepository = expenseRepository;
        this.journeyRepository = journeyRepository;
    }

    /**
     * Retrieves all expenseRepositorys for a specific journey that belong to a given user.
     *
     * @param journeyId the ID of the journey
     * @param userId    the ID of the user
     * @return a list of expenseRepositorys associated with the journey and user
     */
    public List<Expense> getAllExpenseByJourneyId(Long journeyId, Long userId) {
        return expenseRepository.findAllByJourney_JourneyIdAndJourney_User_Id(journeyId, userId);
    }

    /**
     * Retrieves an expenseRepository by its ID.
     *
     * @param expenseRepositoryId the ID of the expenseRepository
     * @return an Optional containing the expenseRepository if found, or empty otherwise
     */
    public Optional<Expense> getExpenseById(Long expenseRepositoryId) {
        return expenseRepository.findByExpenseId(expenseRepositoryId);
    }

    /**
     * Creates a new expenseRepository for a given journey.
     *
     * @param journeyId   the ID of the journey
     * @param expense the expenseRepository to be created
     * @return the created expenseRepository
     * @throws IllegalArgumentException if the journey is not found
     */
    public Expense createExpense(Long journeyId, Expense expense) {
        Optional<Journey> journeyOpt = journeyRepository.findById(journeyId);
        if (journeyOpt.isPresent()) {
            Journey journey = journeyOpt.get();
            expense.setJourney(journey);
            return expenseRepository.save(expense);
        } else {
            throw new IllegalArgumentException("Journey not found for id: " + journeyId);
        }
    }

    /**
     * Updates an existing expenseRepository.
     *
     * @param expenseRepositoryId     the ID of the expenseRepository to update
     * @param updatedExpense the updated expenseRepository data
     * @return an Optional containing the updated expenseRepository if the update was successful, or empty otherwise
     */
    public Optional<Expense> updateExpense(Long expenseRepositoryId, Expense updatedExpense) {
        return expenseRepository.findByExpenseId(expenseRepositoryId).map(expense -> {
            expense.setName(updatedExpense.getName());
            expense.setAmount(updatedExpense.getAmount());
            expense.setDate(updatedExpense.getDate());
            return expenseRepository.save(expense);
        });
    }

    /**
     * Deletes an expenseRepository by its ID.
     *
     * @param expenseRepositoryId the ID of the expenseRepository to delete
     * @return true if the expenseRepository was deleted successfully, false otherwise
     */
    public boolean deleteExpense(Long expenseRepositoryId) {
        if (expenseRepository.existsById(expenseRepositoryId)) {
            expenseRepository.deleteById(expenseRepositoryId);
            return true;
        }
        return false;
    }
}