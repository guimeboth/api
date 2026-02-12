package br.com.gms.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.gms.api.exception.TaskException;
import br.com.gms.api.model.Task;
import br.com.gms.api.model.valueobject.TaskDTO;

public class TaskService {

    private List<Task> tasks = new ArrayList<>();

    public Task createTask(TaskDTO payload) {
        Task task = payload.toTask();
        tasks.add(task);

        return task;
    }

    public void changeDescription(Task task, String newDescription) {
        task.changeDescription(newDescription);
    }

    public void changeScheduledDate(Task task, LocalDateTime newScheduledDate) {
        task.changeScheduledDate(newScheduledDate);
    }

    public void conclude(Task task) {
        task.conclude(Boolean.TRUE);
    }

    public void reopen(Task task) {
        task.reopen(Boolean.FALSE);
    }

    public List<Task> findAll() {
        return tasks;
    }

    public Task findById(UUID id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findAny()
                .orElseThrow(() -> new TaskException("Tarefa não encontrada"));
    }

    public Optional<Task> findByDescription(String description) {
        return tasks.stream().filter(t -> t.getDescription().equalsIgnoreCase(description)).findFirst();
    }

    public List<Task> findByScheduledDate(LocalDateTime scheduledDate) {
        return tasks.stream().filter(t -> t.getScheduledDate().equals(scheduledDate)).toList();
    }

    public void deleteById(UUID id) {
        tasks.removeIf(t -> t.getId().equals(id));
    }

}
