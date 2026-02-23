package br.com.gms.api.model.builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import br.com.gms.api.controller.TaskResponseDTO;
import br.com.gms.api.model.valueobject.CreateTaskDTO;
import br.com.gms.api.model.valueobject.UpdateTaskDTO;

public class TaskDTOTestBuilder {

    private UUID id = UUID.randomUUID();
    private String description = "Tarefa padrão";
    private LocalDateTime scheduledDate = LocalDateTime.of(
            LocalDate.of(2026, 3, 15),
            LocalTime.of(9, 0));

    private TaskDTOTestBuilder() {

    }

    public static TaskDTOTestBuilder aTask() {
        return new TaskDTOTestBuilder();
    }

    public TaskDTOTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public TaskDTOTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskDTOTestBuilder withScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
        return this;
    }

    public TaskDTOTestBuilder withoutScheduledDate() {
        this.scheduledDate = null;
        return this;
    }

    public CreateTaskDTO buildCreateTask() {
        return new CreateTaskDTO(description, scheduledDate);
    }

    public UpdateTaskDTO buildUpdateTask() {
        return new UpdateTaskDTO(description, scheduledDate);
    }

    public TaskResponseDTO buildResponseTask() {
        return new TaskResponseDTO(id, description, scheduledDate, scheduledDate, null);
    }

}
