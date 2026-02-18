package br.com.gms.api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.gms.api.model.valueobject.CreateTaskDTO;
import br.com.gms.api.model.valueobject.UpdateTaskDTO;
import br.com.gms.api.service.TaskService;

public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    public UUID create(CreateTaskDTO payload) {
        return service.create(payload).getId();
    }

    public void update(UpdateTaskDTO payload) {
        service.update(payload);
    }

    public void conclude(UUID id) {
        service.concludeTask(id);
    }

    public void reopen(UUID id) {
        service.reopenTask(id);
    }

    public List<TaskResponseDTO> findAll() {
        return service.findAll().stream().map(TaskResponseDTO::from).toList();
    }

    public TaskResponseDTO findById(UUID id) {
        return TaskResponseDTO.from(service.findById(id));
    }

    public List<TaskResponseDTO> findByScheduledDate(LocalDateTime scheduledDate) {
        return service.findByScheduledDate(scheduledDate).stream().map(TaskResponseDTO::from).toList();
    }

    public void deleteById(UUID id) {
        service.deleteById(id);
    }

}
