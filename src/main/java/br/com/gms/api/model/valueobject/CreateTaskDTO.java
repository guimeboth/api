package br.com.gms.api.model.valueobject;

import java.time.LocalDateTime;

import br.com.gms.api.model.Task;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

public record CreateTaskDTO(@NotBlank String description, @Future LocalDateTime scheduledDate) {

    public Task toTask() {
        return new Task(description, scheduledDate);
    }

}
