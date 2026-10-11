package io.baozhu.dataplatform.quality;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quality/rules")
public class QualityRuleController {
    private final QualityRuleRepository repository;
    public QualityRuleController(QualityRuleRepository repository) { this.repository = repository; }
    @GetMapping public List<QualityRule> list() { return repository.findAll(); }
    @GetMapping("/{id}") public QualityRule get(@PathVariable Long id) { return repository.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public QualityRule create(@Valid @RequestBody CreateQualityRuleRequest request) { return repository.save(request); }
}
