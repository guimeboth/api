package br.com.gms.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.gms.api.exception.BusinessException;

public class Task {

    private UUID id;
    private String description;
    private LocalDateTime scheduledDate;
    private LocalDateTime creationDate;
    private Boolean completed;

    public Task(String description, LocalDateTime scheduledDate) {
        this.id = UUID.randomUUID();
        this.description = validateDescription(description);
        this.creationDate = LocalDateTime.now();
        this.completed = Boolean.FALSE;
        this.scheduledDate = validateScheduledDate(scheduledDate);
    }

    private LocalDateTime validateScheduledDate(LocalDateTime scheduledDate) {
        if (scheduledDate != null && scheduledDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Data agendada deve ser maior que hoje");
        }
        return scheduledDate;
    }

    private String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException("Descrição obrigatória");
        }
        return description;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void changeDescription(String newDescription) {
        this.description = validateDescription(newDescription);
    }

    public void changeScheduledDate(LocalDateTime newScheduledDate) {
        this.scheduledDate = validateScheduledDate(newScheduledDate);
    }

    public void conclude() {
        if (Boolean.TRUE.equals(this.completed)) {
            throw new BusinessException("Tarefa já concluída");
        }
        this.completed = Boolean.TRUE;
    }

    public void reopen() {
        if (Boolean.FALSE.equals(this.completed)) {
            throw new BusinessException("Tarefa já aberta");
        }
        this.completed = Boolean.FALSE;
    }

    @Override
    public String toString() {
        return """
                Task {
                  id: %s
                  description: %s
                  scheduledDate: %s
                  creationDate: %s
                  completed: %s
                }
                """.formatted(id, description, scheduledDate, creationDate, Boolean.TRUE.equals(completed));
    }

}
