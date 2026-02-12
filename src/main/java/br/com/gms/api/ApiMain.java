package br.com.gms.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.gms.api.controller.TaskController;
import br.com.gms.api.model.Task;
import br.com.gms.api.model.valueobject.TaskDTO;

public class ApiMain {

    private static final Logger log = LoggerFactory.getLogger(ApiMain.class);
    private static final TaskController controller = new TaskController();

    public static void main(String[] args) {

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<TaskDTO> payload = List.of(
                new TaskDTO("Preparar café da manhã", LocalDateTime.of(tomorrow, LocalTime.of(5, 0))),
                new TaskDTO("Levar Ana para trabalo", LocalDateTime.of(tomorrow, LocalTime.of(6, 40))),
                new TaskDTO("Levar Otávio para escolinha", LocalDateTime.of(tomorrow, LocalTime.of(7, 0))),
                new TaskDTO("Limpar caixas de areia", LocalDateTime.of(tomorrow, LocalTime.of(7, 15))),
                new TaskDTO("Escovar a Alina", LocalDateTime.of(tomorrow, LocalTime.of(7, 25))),
                new TaskDTO("Lavar louça", LocalDateTime.of(tomorrow, LocalTime.of(7, 35))),
                new TaskDTO("Ligar robô", LocalDateTime.of(tomorrow, LocalTime.of(7, 55))),
                new TaskDTO("Fazer exercícios", LocalDateTime.of(tomorrow, LocalTime.of(8, 0))),
                new TaskDTO("Retirar lixo", LocalDateTime.of(tomorrow, LocalTime.of(8, 45))),
                new TaskDTO("Trabalhar", LocalDateTime.of(tomorrow, LocalTime.of(9, 00))),
                new TaskDTO("Estudar por pelo menos 1 hora", null));

        payload.stream().forEach(controller::createTask);

        printTasks();

        Optional<Task> optional = Optional.empty();

        optional = controller.findByDescription("Levar Ana para trabalo");
        if (optional.isPresent()) {
            Task task = optional.get();
            controller.changeDescription(task, "Levar Ana para trabalho");
            printTask(task.getId());

        }

        optional = Optional.empty();
        optional = controller.findByDescription("Retirar lixo");
        if (optional.isPresent()) {
            Task task = optional.get();
            controller.changeScheduledDate(task, task.getScheduledDate().plusMinutes(5));
            printTask(task.getId());
        }

        optional = Optional.empty();
        optional = controller.findByDescription("Estudar por pelo menos 1 hora");
        if (optional.isPresent()) {
            Task task = optional.get();
            controller.conclude(task);
            printTask(task.getId());

            controller.reopen(task);
            printTask(task.getId());
        }

        TaskDTO newTask = new TaskDTO("Tarefa criada errada", null);
        UUID newTaskId = controller.createTask(newTask);
        printTasks();

        controller.deleteById(newTaskId);
        printTasks();

        printTask(newTaskId);

    }

    private static void printTask(UUID id) {
        Task task = controller.findById(id);

        if (log.isInfoEnabled()) {
            log.info(task.toString());
        }
    }

    private static void printTasks() {
        List<Task> all = controller.findAll();

        if (log.isInfoEnabled()) {
            all.stream().forEach(t -> log.info(t.toString()));
        }
    }

}
