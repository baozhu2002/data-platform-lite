package io.baozhu.dataplatform;

import io.baozhu.dataplatform.source.CreateDataSourceRequest;
import io.baozhu.dataplatform.source.DataSource;
import io.baozhu.dataplatform.source.DataSourceRepository;
import io.baozhu.dataplatform.task.CreateSyncTaskRequest;
import io.baozhu.dataplatform.task.SyncTask;
import io.baozhu.dataplatform.task.SyncTaskRepository;
import io.baozhu.dataplatform.execution.TaskExecutionService;
import io.baozhu.dataplatform.execution.TaskRun;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataPlatformApplicationTests {
    @Autowired DataSourceRepository repository;
    @Autowired SyncTaskRepository taskRepository;
    @Autowired TaskExecutionService executionService;

    @Test
    void contextLoadsAndCreatesDataSource() {
        DataSource source = repository.save(new CreateDataSourceRequest("orders-db", "MYSQL", "jdbc:mysql://localhost/orders"));
        assertThat(source.id()).isNotNull();
        assertThat(repository.findAll()).extracting(DataSource::name).contains("orders-db");
    }
    @Test
    void executionIsIdempotentForSameKey() {
        DataSource source = repository.save(new CreateDataSourceRequest("orders-db-2", "MYSQL", "jdbc:mysql://localhost/orders"));
        SyncTask task = taskRepository.save(new CreateSyncTaskRequest("sync-orders-2", source.id(), "ods_order", "FULL", "0 0 1 * * ?"));
        TaskRun first = executionService.execute(task.id(), "same-request");
        TaskRun second = executionService.execute(task.id(), "same-request");
        assertThat(first.id()).isEqualTo(second.id());
        assertThat(second.status()).isEqualTo("SUCCESS");
    }
}
