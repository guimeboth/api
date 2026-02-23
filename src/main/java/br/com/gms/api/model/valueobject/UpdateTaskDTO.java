package br.com.gms.api.model.valueobject;

import java.time.LocalDateTime;

public record UpdateTaskDTO(String description, LocalDateTime scheduledDate) {

}
