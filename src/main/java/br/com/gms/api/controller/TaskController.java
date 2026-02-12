package br.com.gms.api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.gms.api.model.Task;
import br.com.gms.api.model.valueobject.TaskDTO;
import br.com.gms.api.service.TaskService;

public class TaskController {

    private TaskService service = new TaskService();

    public UUID createTask(TaskDTO payload) {
        try {
            return service.createTask(payload).getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void changeDescription(Task task, String newDescription) {
        try {
            service.changeDescription(task, newDescription);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void changeScheduledDate(Task task, LocalDateTime scheduledDate) {
        try {
            service.changeScheduledDate(task, scheduledDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void conclude(Task task) {
        try {
            service.conclude(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void reopen(Task task) {
        try {
            service.reopen(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Task> findAll() {
        try {
            return service.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Task findById(UUID id) {
        try {
            return service.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<Task> findByDescription(String description) {
        try {
            return service.findByDescription(description);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Task> findByScheduledDate(LocalDateTime scheduledDate) {
        try {
            return service.findByScheduledDate(scheduledDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteById(UUID id) {
        try {
            service.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
