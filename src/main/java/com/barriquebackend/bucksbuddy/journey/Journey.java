package com.barriquebackend.bucksbuddy.journey;

import com.barriquebackend.bucksbuddy.journey.expense.Expense;
import com.barriquebackend.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journeys")
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long journeyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String homeCurr;

    @Column(nullable = false)
    private String vacCurr;

    @Column(nullable = false)
    private int budget;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Expense> expenses = new ArrayList<>();

    // Getters and setters
    public Long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHomeCurr() {
        return homeCurr;
    }

    public void setHomeCurr(String homeCurr) {
        this.homeCurr = homeCurr;
    }

    public String getVacCurr() {
        return vacCurr;
    }

    public void setVacCurr(String vacCurr) {
        this.vacCurr = vacCurr;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    // Helper method to add an expense to the list
    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setJourney(this);
    }

    // Helper method to remove an expense from the list
    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setJourney(null);
    }
}