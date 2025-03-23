package com.barriquebackend.bucksbuddy.journey.expense;

import com.barriquebackend.bucksbuddy.journey.Journey;
import com.barriquebackend.bucksbuddy.journey.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling business logic related to expenses.
 * Provides methods for creating, retrieving, updating, and deleting expenses
 * that are associated with a specific journey and user.
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final JourneyRepository journeyRepository;

    /**
     * Constructs an ExpenseService with the given repositories.
     *
     * @param expenseRepository the repository for performing CRUD operations on expenses
     * @param journeyRepository the repository for retrieving journeys
     */
    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, JourneyRepository journeyRepository) {
        this.expenseRepository = expenseRepository;
        this.journeyRepository = journeyRepository;
    }

    /**
     * Retrieves all expenses for a specific journey that belong to a given user.
     *
     * @param journeyId the ID of the journey
     * @param userId    the ID of the user
     * @return a list of expenses associated with the journey and user
     */
    public List<Expense> getAllExpenseByJourneyId(Long journeyId, Long userId) {
        return expenseRepository.findAllByJourney_JourneyIdAndJourney_User_Id(journeyId, userId);
    }

    /**
     * Retrieves an expense by its ID.
     *
     * @param expenseId the ID of the expense
     * @return an Optional containing the expense if found, or empty otherwise
     */
    public Optional<Expense> getExpenseById(Long expenseId) {
        return expenseRepository.findByExpenseId(expenseId);
    }

    /**
     * Creates a new expense for a given journey.
     *
     * @param journeyId the ID of the journey
     * @param expense the expense to be created
     * @return the created expense
     * @throws IllegalArgumentException if the journey is not found
     */
    public Expense createExpense(Long journeyId, Expense expense) {
        Optional<Journey> journeyOpt = journeyRepository.findById(journeyId);
        if (journeyOpt.isPresent()) {
            Journey journey = journeyOpt.get();
            expense.setJourney(journey);

            // Add the expense to the journey's expense list
            journey.addExpense(expense);

            return expenseRepository.save(expense);
        } else {
            throw new IllegalArgumentException("Journey not found for id: " + journeyId);
        }
    }

    /**
     * Updates an existing expense.
     *
     * @param expenseId the ID of the expense to update
     * @param updatedExpense the updated expense data
     * @return an Optional containing the updated expense if the update was successful, or empty otherwise
     */
    public Optional<Expense> updateExpense(Long expenseId, Expense updatedExpense) {
        return expenseRepository.findByExpenseId(expenseId).map(expense -> {
            expense.setName(updatedExpense.getName());
            expense.setAmount(updatedExpense.getAmount());
            expense.setDate(updatedExpense.getDate());
            return expenseRepository.save(expense);
        });
    }

    /**
     * Deletes an expense by its ID.
     *
     * @param expenseId the ID of the expense to delete
     * @return true if the expense was deleted successfully, false otherwise
     */
    public boolean deleteExpense(Long expenseId) {
        Optional<Expense> expenseOpt = expenseRepository.findByExpenseId(expenseId);

        if (expenseOpt.isPresent()) {
            Expense expense = expenseOpt.get();
            Journey journey = expense.getJourney();

            // Remove the expense from the journey's expense list
            if (journey != null) {
                journey.removeExpense(expense);
            }

            expenseRepository.deleteById(expenseId);
            return true;
        }
        return false;
    }
}