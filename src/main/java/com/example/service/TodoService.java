package com.example.service;

import com.example.entity.Todo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class TodoService {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(Todo todo) {
        entityManager.persist(todo);
    }

    public void update(Todo todo) {
        entityManager.merge(todo);
    }

    public Todo findById(Long id) {
        return entityManager.find(Todo.class, id);
    }

    public List<Todo> findAll() {
        return entityManager.createQuery("Select t from Todo t", Todo.class).getResultList();
    }
}
