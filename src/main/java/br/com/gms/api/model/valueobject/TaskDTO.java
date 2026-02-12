package br.com.gms.api.model.valueobject;

import java.time.LocalDateTime;

import br.com.gms.api.model.Task;

public record TaskDTO(String description, LocalDateTime scheduledDate) {

    public Task toTask() {
        return new Task(description(), scheduledDate());
    }

}
