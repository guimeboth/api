package br.com.gms.api.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gms.api.model.Task;
import br.com.gms.api.model.valueobject.CreateTaskDTO;
import br.com.gms.api.model.valueobject.UpdateTaskDTO;
import br.com.gms.api.service.TaskService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateTaskDTO payload) {
        Task task = service.create(payload);
        URI uri = URI.create("/tasks/" + task.getId());

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateTaskDTO payload) {
        service.update(id, payload);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/conclude")
    public ResponseEntity<Void> conclude(@PathVariable UUID id) {
        service.concludeTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<Void> reopen(@PathVariable UUID id) {
        service.reopenTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> findAll() {
        List<TaskResponseDTO> list = service.findAll().stream().map(TaskResponseDTO::from).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(TaskResponseDTO.from(service.findById(id)));
    }

    @GetMapping("/by-scheduled-date")
    public ResponseEntity<List<TaskResponseDTO>> findByScheduledDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledDate) {
        return ResponseEntity
                .ok(service.findByScheduledDate(scheduledDate).stream().map(TaskResponseDTO::from).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
