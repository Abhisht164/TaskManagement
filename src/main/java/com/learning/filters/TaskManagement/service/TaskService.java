package com.learning.filters.TaskManagement.service;


import com.learning.filters.TaskManagement.entity.Task;
import com.learning.filters.TaskManagement.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(Task task){
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedFields){

        Task task1 = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        task1.setTitle(updatedFields.getTitle());
        task1.setDescription(updatedFields.getDescription());
        task1.setUpdatedAt(LocalDateTime.now());
        task1.setStatus(updatedFields.getStatus());
        return taskRepository.save(task1);
    }

    public Task getTaskById(Long id){

       return  taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No Task present to update"));
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public void deleteTask(Long id){
        taskRepository.deleteById(id);
    }
}
