package com.example.todoapp.presentation.dto.response;

import com.example.todoapp.domain.model.TodoPriority;
import com.example.todoapp.domain.model.TodoStatus;
import com.example.todoapp.infrastructure.persistence.entity.TodoEntity;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * TODOレスポンス
 */
public record TodoResponse(
    Long id,
    String title,
    String description,
    TodoStatus status,
    TodoPriority priority,
    LocalDate dueDate,
    Long parentId,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {
    /**
     * Entityから生成
     */
    public static TodoResponse from(TodoEntity entity) {
        return new TodoResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getPriority(),
            entity.getDueDate(),
            entity.getParentId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}