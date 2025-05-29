package com.example.todoapp.application.service;

import com.example.todoapp.common.exception.TodoNotFoundException;
import com.example.todoapp.domain.model.TodoStatus;
import com.example.todoapp.domain.repository.TodoRepository;
import com.example.todoapp.infrastructure.persistence.entity.TodoEntity;
import com.example.todoapp.presentation.dto.request.CreateTodoRequest;
import com.example.todoapp.presentation.dto.request.UpdateTodoRequest;
import com.example.todoapp.presentation.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TODOサービス
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * TODO作成
     */
    @Transactional
    public TodoResponse createTodo(CreateTodoRequest request) {
        log.debug("Creating new TODO: {}", request.title());
        
        TodoEntity todo = new TodoEntity(
            request.title(),
            request.description(),
            TodoStatus.TODO,
            request.priority(),
            request.dueDate()
        );
        
        TodoEntity saved = todoRepository.save(todo);
        log.info("Created TODO with id: {}", saved.getId());
        
        return TodoResponse.from(saved);
    }

    /**
     * TODO取得（ID指定）
     */
    public TodoResponse getTodo(Long id) {
        log.debug("Getting TODO with id: {}", id);
        
        TodoEntity todo = todoRepository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));
            
        return TodoResponse.from(todo);
    }

    /**
     * TODO一覧取得（ページング）
     */
    public Page<TodoResponse> getTodos(Pageable pageable) {
        log.debug("Getting TODO list with pageable: {}", pageable);
        
        Page<TodoEntity> todos = todoRepository.findAll(pageable);
        return todos.map(TodoResponse::from);
    }

    /**
     * ステータスでTODO一覧取得
     */
    public List<TodoResponse> getTodosByStatus(TodoStatus status) {
        log.debug("Getting TODOs with status: {}", status);
        
        List<TodoEntity> todos = todoRepository.findByStatus(status);
        return todos.stream()
            .map(TodoResponse::from)
            .toList();
    }

    /**
     * TODO更新
     */
    @Transactional
    public TodoResponse updateTodo(Long id, UpdateTodoRequest request) {
        log.debug("Updating TODO with id: {}", id);
        
        TodoEntity todo = todoRepository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));
            
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setStatus(request.status());
        todo.setPriority(request.priority());
        todo.setDueDate(request.dueDate());
        
        TodoEntity updated = todoRepository.save(todo);
        log.info("Updated TODO with id: {}", updated.getId());
        
        return TodoResponse.from(updated);
    }

    /**
     * TODO削除
     */
    @Transactional
    public void deleteTodo(Long id) {
        log.debug("Deleting TODO with id: {}", id);
        
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        
        todoRepository.deleteById(id);
        log.info("Deleted TODO with id: {}", id);
    }
}