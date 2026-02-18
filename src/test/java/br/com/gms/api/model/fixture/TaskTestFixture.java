package br.com.gms.api.model.fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import br.com.gms.api.model.builder.TaskDTOTestBuilder;
import br.com.gms.api.model.valueobject.CreateTaskDTO;
import br.com.gms.api.service.TaskService;

public class TaskTestFixture {

    private TaskTestFixture() {
    }

    public static List<CreateTaskDTO> createTasks(
            TaskService service,
            int amount) {
        return IntStream.range(0, amount)
                .mapToObj(i -> {
                    CreateTaskDTO dto = TaskDTOTestBuilder.aTask()
                            .withDescription("Tarefa " + i)
                            .withScheduledDate(LocalDateTime.now().plusDays(1).plusHours(2))
                            .buildCreateTask();

                    service.create(dto);
                    return dto;
                })
                .toList();
    }

}
