package br.com.gms.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gms.api.exception.BusinessException;
import br.com.gms.api.exception.TaskNotFoundException;
import br.com.gms.api.model.Task;
import br.com.gms.api.model.valueobject.CreateTaskDTO;
import br.com.gms.api.model.valueobject.UpdateTaskDTO;
import br.com.gms.api.service.TaskService;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TaskService taskService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test // Isso aqui é um caso de teste executável pelo JUnit
        void shouldCreateTaskAndReturn201() throws Exception {

                // Esse é o corpo da requisição de create
                CreateTaskDTO payload = new CreateTaskDTO("Estudar MockMvc", LocalDateTime.now().plusDays(1));
                // Vou simular que o service criou uma Task válida
                Task task = new Task("Estudar MockMvc", LocalDateTime.now().plusDays(1));

                // Quando o controller chamar taskService.create(...),independente do DTO
                // recebido, devolva essa task
                when(taskService.create(any(CreateTaskDTO.class)))
                                .thenReturn(task);

                // Simule um POST HTTP para /tasks
                mockMvc.perform(
                                post("/tasks")
                                                // O corpo da requisição é JSON
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // Esse JSON é o corpo da requisição
                                                .content(objectMapper.writeValueAsString(payload)))
                                // Espero que a resposta HTTP seja 201 Created
                                .andExpect(status().isCreated())
                                // Espero que a resposta contenha o header Location
                                .andExpect(header().exists("Location"));
        }

        @Test
        void shouldUpdateTaskAndReturn204() throws Exception {

                // Vou simular o ID vindo da URL (/tasks/{id})
                UUID id = UUID.randomUUID();
                Task task = new Task("Descrição antiga", null);

                // Esse é o corpo da requisição de update
                UpdateTaskDTO payload = new UpdateTaskDTO("Descrição atualizada",
                                LocalDateTime.of(2030, 02, 01, 10, 0));

                // Quando o controller chamar taskService.update(...),independente do DTO
                // recebido, devolva essa task
                when(taskService.update(eq(id), any(UpdateTaskDTO.class))).thenReturn(task);

                // Simule um PUT em /tasks/{id} substituindo pelo UUID
                mockMvc.perform(
                                put("/tasks/{id}", id)
                                                // O corpo da requisição é JSON
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // Esse JSON é o corpo da requisição
                                                .content(objectMapper.writeValueAsString(payload)))
                                // Espero que a resposta HTTP seja 204 No Content
                                .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404WhenUpdatingNonExistingTask() throws Exception {

                UUID id = UUID.randomUUID();
                UpdateTaskDTO payload = new UpdateTaskDTO("Tarefa alterada", null);

                doThrow(new TaskNotFoundException(id)).when(taskService).update(eq(id), any(UpdateTaskDTO.class));

                mockMvc.perform(put("/tasks/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Tarefa não encontrada. Id: " + id));
        }

        @Test
        void shouldReturn422WhenUpdatingWithPastScheduledDate() throws Exception {

                UUID id = UUID.randomUUID();
                UpdateTaskDTO payload = new UpdateTaskDTO(
                                null,
                                LocalDateTime.now().minusDays(1));

                doThrow(new BusinessException("Data agendada deve ser maior que hoje")).when(taskService).update(eq(id),
                                any());

                mockMvc.perform(put("/tasks/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)))
                                .andExpect(status().isUnprocessableEntity());
        }

        @Test
        void shouldReturn422WhenUpdatingTaskWithInvalidDescription() throws Exception {

                UUID id = UUID.randomUUID();
                UpdateTaskDTO payload = new UpdateTaskDTO("", null);

                doThrow(new BusinessException("Descrição obrigatória")).when(taskService).update(eq(id),
                                any(UpdateTaskDTO.class));

                mockMvc.perform(put("/tasks/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(jsonPath("$.message").value("Descrição obrigatória"));
        }

        @Test
        void shouldConcludeTaskAndReturn204() throws Exception {

                // Vou simular o ID vindo da URL (/tasks/{id}/conclude)
                UUID id = UUID.randomUUID();

                // Quando o controller chamar taskService.concludeTask(id), não faça nada
                doNothing().when(taskService).concludeTask(id);

                // Simule um PATCH em /tasks/{id}/conclude substituindo pelo UUID
                mockMvc.perform(
                                patch("/tasks/{id}/conclude", id))
                                // Espero que a resposta HTTP seja 204 No Content
                                .andExpect(status().isNoContent());

                // Quando esse endpoint do controller é chamado, ele precisa delegar para esse
                // método do service, com esse argumento.
                verify(taskService).concludeTask(id);
        }

        @Test
        void shouldReturn404WhenCompletingNonExistingTask() throws Exception {

                UUID id = UUID.randomUUID();
                doThrow(new TaskNotFoundException(id)).when(taskService).concludeTask(id);

                mockMvc.perform(patch("/tasks/{id}/conclude", id)).andExpect(status().isNotFound());

                verify(taskService).concludeTask(id);
        }

        @Test
        void shouldReturn422WhenCompletingTaskCompleted() throws Exception {

                UUID id = UUID.randomUUID();
                doThrow(new BusinessException("Tarefa já concluída")).when(taskService).concludeTask(id);

                mockMvc.perform(patch("/tasks/{id}/conclude", id)).andExpect(status().isUnprocessableEntity());

                verify(taskService).concludeTask(id);
        }

        @Test
        void shouldReopenTaskAndReturn204() throws Exception {

                // Vou simular o ID vindo da URL (/tasks/{id}/reopen)
                UUID id = UUID.randomUUID();

                // Quando o controller chamar taskService.reopenTask(id), não faça nada
                doNothing().when(taskService).reopenTask(id);

                // Simule um PATCH em /tasks/{id}/reopen substituindo pelo UUID
                mockMvc.perform(patch("/tasks/{id}/reopen", id))
                                // Espero que a resposta HTTP seja 204 No Content
                                .andExpect(status().isNoContent());

                // Quando esse endpoint do controller é chamado, ele precisa delegar para esse
                // método do service, com esse argumento.
                verify(taskService).reopenTask(id);
        }

        @Test
        void shouldReturn404WhenReopeningNonExistingTask() throws Exception {

                UUID id = UUID.randomUUID();
                doThrow(new TaskNotFoundException(id)).when(taskService).reopenTask(id);

                mockMvc.perform(patch("/tasks/{id}/reopen", id)).andExpect(status().isNotFound());

                verify(taskService).reopenTask(id);
        }

        @Test
        void shouldReturn422WhenReopeningOpenTask() throws Exception {

                UUID id = UUID.randomUUID();
                doThrow(new BusinessException("Tarefa já aberta")).when(taskService).reopenTask(id);

                mockMvc.perform(patch("/tasks/{id}/reopen", id)).andExpect(status().isUnprocessableEntity());

                verify(taskService).reopenTask(id);
        }

        @Test
        void shouldReturnListOfTasks() throws Exception {

                Task task1 = new Task("Tarefa 1", LocalDateTime.of(LocalDate.of(2030, 1, 1), LocalTime.now()));
                Task task2 = new Task("Tarefa 2", null);

                // Quando o controller chamar o service, devolva essa lista.
                when(taskService.findAll()).thenReturn(List.of(task1, task2));

                // Simule um GET em /tasks
                mockMvc.perform(get("/tasks"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                // Pegue o JSON da resposta, vá até a raiz, descubra quantos elementos existem e
                                // verifique se são 2.
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].description").value("Tarefa 1"))
                                .andExpect(jsonPath("$[1].description").value("Tarefa 2"));
        }

        @Test
        void shouldReturnTaskById() throws Exception {

                Task task = new Task("Tarefa padrão", LocalDateTime.of(2030, 1, 1, 10, 0));
                UUID id = task.getId();

                when(taskService.findById(id)).thenReturn(task);

                mockMvc.perform(get("/tasks/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(id.toString()))
                                .andExpect(jsonPath("$.description").value("Tarefa padrão"))
                                .andExpect(jsonPath("$.scheduledDate").value("2030-01-01T10:00:00"));
        }

        @Test
        void shouldReturn404WhenSearchingNonExistingTask() throws Exception {

                UUID id = UUID.randomUUID();

                doThrow(new TaskNotFoundException(id)).when(taskService).findById(id);

                mockMvc.perform(get("/tasks/{id}", id)).andExpect(status().isNotFound());

                verify(taskService).findById(id);
        }

        @Test
        void shouldReturnTasksFilteredByScheduledDate() throws Exception {

                LocalDateTime scheduledDate = LocalDateTime.of(2030, 1, 1, 10, 0);

                Task task = new Task("Estudar filtros", scheduledDate);

                when(taskService.findByScheduledDate(scheduledDate)).thenReturn(List.of(task));

                mockMvc.perform(get("/tasks/by-scheduled-date").param("scheduledDate", "2030-01-01T10:00:00"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].description").value("Estudar filtros"))
                                .andExpect(jsonPath("$[0].scheduledDate").value("2030-01-01T10:00:00"));
        }

        @Test
        void shouldReturnTasksWhenScheduledDateIsNotProvided() throws Exception {

                when(taskService.findByScheduledDate(null))
                                .thenReturn(List.of());

                mockMvc.perform(get("/tasks/by-scheduled-date"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void shouldReturn400WhenScheduledDateHasInvalidFormat() throws Exception {

                mockMvc.perform(get("/tasks/by-scheduled-date")
                                .param("scheduledDate", "abc"))
                                .andExpect(status().isBadRequest());

                verify(taskService, never()).findByScheduledDate(any());
        }

        @Test
        void shouldDeleteTaskAndReturn204() throws Exception {

                UUID id = UUID.randomUUID();

                doNothing().when(taskService).deleteById(id);

                mockMvc.perform(delete("/tasks/{id}", id))
                                .andExpect(status().isNoContent());

                verify(taskService).deleteById(id);
        }

        @Test
        void shouldReturn404WhenDeletingNonExistingTask() throws Exception {

                UUID id = UUID.randomUUID();

                doThrow(new TaskNotFoundException(id)).when(taskService).deleteById(id);

                mockMvc.perform(delete("/tasks/{id}", id)).andExpect(status().isNotFound());

                verify(taskService).deleteById(id);
        }

}
