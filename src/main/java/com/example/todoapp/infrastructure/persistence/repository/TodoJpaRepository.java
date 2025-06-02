package com.example.todoapp.infrastructure.persistence.repository;

import com.example.todoapp.domain.model.TodoPriority;
import com.example.todoapp.domain.model.TodoStatus;
import com.example.todoapp.infrastructure.persistence.entity.TodoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * TODO JpaRepository
 */
@Repository
public interface TodoJpaRepository extends JpaRepository<TodoEntity, Long> {
    
    /**
     * ステータスでTODOを検索する
     */
    List<TodoEntity> findByStatusOrderByCreatedAtDesc(TodoStatus status);
    
    /**
     * 優先度でTODOを検索する
     */
    List<TodoEntity> findByPriorityOrderByCreatedAtDesc(TodoPriority priority);
    
    /**
     * 期限が指定日以前のTODOを検索する
     */
    @Query("SELECT t FROM TodoEntity t WHERE t.dueDate <= :date AND t.status != :doneStatus ORDER BY t.dueDate ASC")
    List<TodoEntity> findByDueDateBeforeAndNotDone(@Param("date") LocalDate date, 
                                                    @Param("doneStatus") TodoStatus doneStatus);
    
    /**
     * タイトルに部分一致するTODOを検索する
     */
    List<TodoEntity> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);
    
    /**
     * ユーザーIDでTODOを検索する
     */
    List<TodoEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * ユーザーIDとステータスでTODOを検索する
     */
    List<TodoEntity> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, TodoStatus status);
    
    /**
     * ユーザーIDでTODOを検索する（ページング）
     */
    Page<TodoEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * ユーザーIDですべてのTODOを削除する
     */
    void deleteByUserId(Long userId);
    
    /**
     * 親タスクIDで子タスクを検索する
     */
    List<TodoEntity> findByParentIdOrderByCreatedAtDesc(Long parentId);
}