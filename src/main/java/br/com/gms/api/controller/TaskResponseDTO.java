package br.com.gms.api.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.gms.api.model.Task;

public record TaskResponseDTO(UUID id, String description, LocalDateTime scheduledDate, LocalDateTime creationDate,
        Boolean completed) {

    public static TaskResponseDTO from(Task task) {
        return new TaskResponseDTO(task.getId(), task.getDescription(), task.getScheduledDate(), task.getCreationDate(),
                task.getCompleted());
    }

}
