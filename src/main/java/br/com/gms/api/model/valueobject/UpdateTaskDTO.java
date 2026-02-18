package br.com.gms.api.model.valueobject;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateTaskDTO(UUID id, String description, LocalDateTime scheduledDate) {

}
