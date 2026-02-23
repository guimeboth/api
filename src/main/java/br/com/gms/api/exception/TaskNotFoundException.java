package br.com.gms.api.exception;

import java.util.UUID;

public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException(UUID id) {
        super("Tarefa não encontrada. Id: " + id);
    }

}
