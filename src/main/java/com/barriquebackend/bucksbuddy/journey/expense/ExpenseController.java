package com.barriquebackend.bucksbuddy.journey.expense;

import com.barriquebackend.bucksbuddy.journey.Journey;
import com.barriquebackend.bucksbuddy.journey.JourneyService;
import com.barriquebackend.user.User;
import com.barriquebackend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing expenses related to journeys.
 * Provides endpoints to create, retrieve, update, and delete expenses.
 */
@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final JourneyService journeyService;
    private final UserRepository userRepository;

    /**
     * Constructs an ExpenseController with the specified services and user repository.
     *
     * @param expenseService the service for expense business logic
     * @param journeyService     the service for journey business logic
     * @param userRepository     the repository for user data
     */
    @Autowired
    public ExpenseController(ExpenseService expenseService, JourneyService journeyService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.journeyService = journeyService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all expense for a given journey.
     *
     * @param journeyId      the ID of the journey
     * @param authentication the authentication token containing user details
     * @return a ResponseEntity with the list of expense if authorized, or an error status
     */
    @GetMapping("/journey/{journeyId}/expense")
    public ResponseEntity<List<Expense>> getAllExpenseByJourneyId(@PathVariable Long journeyId,
                                                                  Authentication authentication) {
        Optional<Journey> authorizedJourney = getAuthorizedJourney(journeyId, authentication);
        if (authorizedJourney.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = getAuthenticatedUser(authentication);
        List<Expense> expenses = expenseService.getAllExpenseByJourneyId(journeyId, user.getId());
        return ResponseEntity.ok(expenses);
    }

    /**
     * Retrieves an expense by its ID for a given journey.
     *
     * @param journeyId      the ID of the journey
     * @param expenseId  the ID of the expense
     * @param authentication the authentication token containing user details
     * @return a ResponseEntity with the expense if found and authorized, or an error status
     */
    @GetMapping("/journey/{journeyId}/expense/{expenseId}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long journeyId,
                                                  @PathVariable Long expenseId,
                                                  Authentication authentication) {
        Optional<Journey> authorizedJourney = getAuthorizedJourney(journeyId, authentication);
        if (authorizedJourney.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Expense> expenseServiceOpt = expenseService.getExpenseById(expenseId);
        if (expenseServiceOpt.isPresent() && expenseServiceOpt.get().getJourney().getJourneyId().equals(journeyId)) {
            return ResponseEntity.ok(expenseServiceOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new expenseService for a given journey.
     *
     * @param journeyId      the ID of the journey
     * @param expense    the expenseService data to create
     * @param authentication the authentication token containing user details
     * @return a ResponseEntity with the created expenseService if successful, or an error status
     */
    @PostMapping("/journey/{journeyId}/expense")
    public ResponseEntity<Expense> createExpense(@PathVariable Long journeyId,
                                                 @RequestBody Expense expense,
                                                 Authentication authentication) {
        Optional<Journey> authorizedJourney = getAuthorizedJourney(journeyId, authentication);
        if (authorizedJourney.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Expense createdExpense = expenseService.createExpense(journeyId, expense);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }

    /**
     * Updates an existing expenseService for a given journey.
     *
     * @param journeyId      the ID of the journey
     * @param expenseId  the ID of the expenseService to update
     * @param expense    the updated expenseService data
     * @param authentication the authentication token containing user details
     * @return a ResponseEntity with the updated expenseService if successful, or an error status
     */
    @PutMapping("/journey/{journeyId}/expense/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long journeyId,
                                                 @PathVariable Long expenseId,
                                                 @RequestBody Expense expense,
                                                 Authentication authentication) {
        Optional<Journey> authorizedJourney = getAuthorizedJourney(journeyId, authentication);
        if (authorizedJourney.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Expense> existingExp = expenseService.getExpenseById(expenseId);
        if (existingExp.isPresent() && existingExp.get().getJourney().getJourneyId().equals(journeyId)) {
            Optional<Expense> updatedExp = expenseService.updateExpense(expenseId, expense);
            return updatedExp.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes an expenseService for a given journey.
     *
     * @param journeyId      the ID of the journey
     * @param expenseId  the ID of the expenseService to delete
     * @param authentication the authentication token containing user details
     * @return a ResponseEntity with no content if deletion is successful, or an error status
     */
    @DeleteMapping("/journey/{journeyId}/expense/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long journeyId,
                                              @PathVariable Long expenseId,
                                              Authentication authentication) {
        Optional<Journey> authorizedJourney = getAuthorizedJourney(journeyId, authentication);
        if (authorizedJourney.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Expense> expenseServiceOpt = expenseService.getExpenseById(expenseId);
        if (expenseServiceOpt.isPresent() && expenseServiceOpt.get().getJourney().getJourneyId().equals(journeyId)) {
            boolean deleted = expenseService.deleteExpense(expenseId);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Helper method to extract the authenticated user from the security context.
     *
     * @param authentication the authentication token containing user details
     * @return the authenticated User
     */
    private User getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    /**
     * Helper method to verify that the specified journey is owned by the authenticated user.
     *
     * @param journeyId      the ID of the journey to verify
     * @param authentication the authentication token containing user details
     * @return an Optional containing the journey if ownership is confirmed, or an empty Optional otherwise
     */
    private Optional<Journey> getAuthorizedJourney(Long journeyId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Journey journey = journeyService.getJourneyById(journeyId);
        if (!journey.getUser().getId().equals(user.getId())) {
            return Optional.empty();
        }
        return Optional.of(journey);
    }
}