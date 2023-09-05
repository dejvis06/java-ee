package com.example.entity;

import lombok.Data;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Task must be set")
    private String task;

    @NotNull(message = "Due date must be set")
    @FutureOrPresent(message = "Due date must be in the present or the future")
    @JsonbDateFormat(value = "yyyy-MM-dd")
    private LocalDate dueDate;

    private boolean isCompleted;
    private LocalDate dateCompleted;
    private LocalDate dateCreated;

    @PrePersist
    private void init() {
        this.dateCreated = LocalDate.now();
    }

}
