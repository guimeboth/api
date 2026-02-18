package br.com.gms.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.gms.api.exception.TaskNotFoundException;
import br.com.gms.api.model.Task;
import br.com.gms.api.model.valueobject.CreateTaskDTO;
import br.com.gms.api.model.valueobject.UpdateTaskDTO;

public class TaskService {

    private List<Task> tasks = new ArrayList<>();

    public Task create(CreateTaskDTO dto) {
        Task task = dto.toTask();
        tasks.add(task);

        return task;
    }

    public Task update(UpdateTaskDTO dto) {
        Task task = findById(dto.id());

        if (!Objects.equals(dto.description(), task.getDescription())) {
            task.changeDescription(dto.description());
        }

        if (!Objects.equals(dto.scheduledDate(), task.getScheduledDate())) {
            task.changeScheduledDate(dto.scheduledDate());
        }

        return task;
    }

    public void concludeTask(UUID id) {
        Task task = findById(id);
        task.conclude();
    }

    public void reopenTask(UUID id) {
        Task task = findById(id);
        task.reopen();
    }

    public List<Task> findAll() {
        return List.copyOf(tasks);
    }

    public Task findById(UUID id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst().orElseThrow(TaskNotFoundException::new);
    }

    public List<Task> findByScheduledDate(LocalDateTime scheduledDate) {
        return tasks.stream().filter(
                t -> Objects.equals(t.getScheduledDate(), scheduledDate))
                .toList();
    }

    public void deleteById(UUID id) {
        Task task = findById(id);
        tasks.remove(task);
    }

}
