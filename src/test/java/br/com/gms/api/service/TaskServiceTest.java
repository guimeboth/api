package br.com.gms.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.gms.api.exception.TaskException;
import br.com.gms.api.exception.TaskNotFoundException;
import br.com.gms.api.model.Task;
import br.com.gms.api.model.builder.TaskDTOTestBuilder;
import br.com.gms.api.model.fixture.TaskTestFixture;
import br.com.gms.api.model.valueobject.CreateTaskDTO;
import br.com.gms.api.model.valueobject.UpdateTaskDTO;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService();
    }

    @Test
    void shouldCreateTaskWhenDescriptionAndDateProvided() {
        CreateTaskDTO taskDTO = TaskDTOTestBuilder.aTask().buildCreateTask();

        Task created = service.create(taskDTO);

        assertEquals(taskDTO.description(), created.getDescription());
        assertEquals(taskDTO.scheduledDate(), created.getScheduledDate());
    }

    @Test
    void shouldCreateTaskWhenDescription() {
        CreateTaskDTO taskDTO = TaskDTOTestBuilder.aTask().withoutScheduledDate().buildCreateTask();

        Task created = service.create(taskDTO);

        assertEquals(taskDTO.description(), created.getDescription());
        assertNull(created.getScheduledDate());
    }

    @Test
    void shouldUpdateTask() {
        Task created = service.create(TaskDTOTestBuilder.aTask().buildCreateTask());
        UpdateTaskDTO taskDTO = TaskDTOTestBuilder.aTask().withId(created.getId())
                .withDescription("Nova descrição").withScheduledDate(created.getScheduledDate().plusDays(12))
                .buildUpdateTask();

        service.update(taskDTO);

        assertEquals(created.getId(), taskDTO.id());
        assertEquals(created.getDescription(), taskDTO.description());
        assertEquals(created.getScheduledDate(), taskDTO.scheduledDate());
    }

    @Test
    void shouldConcludeTask() {
        Task created = service.create(TaskDTOTestBuilder.aTask().buildCreateTask());

        service.concludeTask(created.getId());

        Task completedTask = service.findById(created.getId());

        assertEquals(created.getId(), completedTask.getId());
        assertTrue(completedTask.getCompleted());
    }

    @Test
    void shouldReopenTask() {
        Task created = service.create(TaskDTOTestBuilder.aTask().buildCreateTask());
        service.concludeTask(created.getId());

        service.reopenTask(created.getId());

        Task reopenedTask = service.findById(created.getId());

        assertEquals(created.getId(), reopenedTask.getId());
        assertFalse(reopenedTask.getCompleted());
    }

    @Test
    void shouldReturnAllTasks() {
        TaskTestFixture.createTasks(service, 5);

        List<Task> tasks = service.findAll();

        assertEquals(5, tasks.size());
    }

    @Test
    void shouldReturnOneTaskAfterCreate() {
        Task created = service.create(TaskDTOTestBuilder.aTask().buildCreateTask());

        Task task = service.findById(created.getId());

        assertEquals(created.getId(), task.getId());
    }

    @Test
    void shouldReturnTasksByScheduledDate() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(2);

        List<CreateTaskDTO> tasks = List.of(
                TaskDTOTestBuilder.aTask().withScheduledDate(futureDate.minusDays(1)).buildCreateTask(),
                TaskDTOTestBuilder.aTask().withScheduledDate(futureDate).buildCreateTask(),
                TaskDTOTestBuilder.aTask().withScheduledDate(futureDate).buildCreateTask(),
                TaskDTOTestBuilder.aTask().withScheduledDate(futureDate).buildCreateTask(),
                TaskDTOTestBuilder.aTask().withScheduledDate(futureDate).buildCreateTask());

        tasks.forEach(service::create);

        List<Task> byScheduledDate = service.findByScheduledDate(futureDate);

        assertEquals(4, byScheduledDate.size());
    }

    @Test
    void shouldReturnTasksWithoutScheduledDateWhenFilterIsNull() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(2);

        List<CreateTaskDTO> tasks = List.of(
                TaskDTOTestBuilder.aTask().withScheduledDate(futureDate.minusDays(1)).buildCreateTask(),
                TaskDTOTestBuilder.aTask().withScheduledDate(futureDate).buildCreateTask(),
                TaskDTOTestBuilder.aTask().withoutScheduledDate().buildCreateTask(),
                TaskDTOTestBuilder.aTask().withoutScheduledDate().buildCreateTask(),
                TaskDTOTestBuilder.aTask().withoutScheduledDate().buildCreateTask());

        tasks.forEach(service::create);

        List<Task> byScheduledDate = service.findByScheduledDate(null);

        assertEquals(3, byScheduledDate.size());
    }

    @Test
    void shouldDeleteTask() {
        Task created = service.create(TaskDTOTestBuilder.aTask().buildCreateTask());

        service.deleteById(created.getId());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void shouldNotCreateTaskWithADateEarlierThanToday() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        CreateTaskDTO taskDTO = TaskDTOTestBuilder.aTask().withScheduledDate(pastDate).buildCreateTask();

        TaskException exception = assertThrows(TaskException.class,
                () -> service.create(taskDTO));

        assertEquals("Data agendada deve ser maior que hoje", exception.getMessage());
    }

    @Test
    void shouldNotCreateTaskWithEmptyDescription() {
        CreateTaskDTO taskDTO = TaskDTOTestBuilder.aTask().withDescription("").buildCreateTask();

        TaskException exception = assertThrows(TaskException.class, () -> service.create(taskDTO));

        assertEquals("Descrição obrigatória", exception.getMessage());
    }

    @Test
    void shouldCompleteTaskAlreadyCompleted() {
        Task created = service.create(TaskDTOTestBuilder.aTask().buildCreateTask());
        created.conclude();

        TaskException exception = assertThrowsExactly(TaskException.class, () -> service.concludeTask(created.getId()));

        assertEquals("Tarefa já concluída", exception.getMessage());
    }

    @Test
    void shouldCompleteTaskAlreadyReopen() {
        Task created = service.create(TaskDTOTestBuilder.aTask().buildCreateTask());

        TaskException exception = assertThrowsExactly(TaskException.class, () -> service.reopenTask(created.getId()));

        assertEquals("Tarefa já aberta", exception.getMessage());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenFindByIdWithNonExistingId() {
        TaskTestFixture.createTasks(service, 3);
        UUID randomUUID = UUID.randomUUID();

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> service.findById(randomUUID));

        assertEquals("Tarefa não encontrada", exception.getMessage());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenUpdatingNonExistingTask() {
        TaskTestFixture.createTasks(service, 3);
        UpdateTaskDTO updatedTask = TaskDTOTestBuilder.aTask().withDescription("Task que não está na lista")
                .buildUpdateTask();

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> service.update(updatedTask));

        assertEquals("Tarefa não encontrada", exception.getMessage());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenConcludingNonExistingTask() {
        TaskTestFixture.createTasks(service, 3);
        UUID randomUUID = UUID.randomUUID();

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> service.concludeTask(randomUUID));

        assertEquals("Tarefa não encontrada", exception.getMessage());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenReopeningNonExistingTask() {
        TaskTestFixture.createTasks(service, 3);
        UUID randomUUID = UUID.randomUUID();

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> service.reopenTask(randomUUID));

        assertEquals("Tarefa não encontrada", exception.getMessage());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenDeletingNonExistingTask() {
        TaskTestFixture.createTasks(service, 3);
        UUID randomUUID = UUID.randomUUID();

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> service.deleteById(randomUUID));

        assertEquals("Tarefa não encontrada", exception.getMessage());
    }

}
