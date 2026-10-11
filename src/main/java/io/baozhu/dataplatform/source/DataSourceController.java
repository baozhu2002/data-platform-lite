package io.baozhu.dataplatform.source;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data-sources")
public class DataSourceController {
    private final DataSourceRepository repository;
    public DataSourceController(DataSourceRepository repository) { this.repository = repository; }

    @GetMapping
    public List<DataSource> list() { return repository.findAll(); }

    @GetMapping("/{id}")
    public DataSource get(@PathVariable Long id) { return repository.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataSource create(@Valid @RequestBody CreateDataSourceRequest request) { return repository.save(request); }
}
