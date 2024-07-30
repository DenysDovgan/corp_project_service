package faang.school.projectservice.controller;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.dto.initiative.InitiativeStatusDto;
import faang.school.projectservice.service.InitiativeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/initiative")
public class InitiativeController {
    private final InitiativeService initiativeService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public InitiativeDto createInitiative(@Valid @RequestBody InitiativeDto initiativeDto) {
        return initiativeService.createInitiative(initiativeDto);
    }

    @PostMapping("/{initiativeId}")
    @ResponseStatus(HttpStatus.OK)
    public InitiativeDto updateInitiative(@PathVariable Long initiativeId,
                                 @RequestBody InitiativeStatusDto initiativeStatusDto) {
        return initiativeService.updateInitiative(initiativeId, initiativeStatusDto);
    }

    @GetMapping("/all/filters")
    public List<InitiativeDto> findAllInitiativesWithFilters(@RequestBody InitiativeFilterDto initiativeFilterDto) {
        return initiativeService.findAllInitiativesWithFilters(initiativeFilterDto);
    }

    @GetMapping("/all")
    public List<InitiativeDto> findAllInitiatives() {
        return initiativeService.findAllInitiatives();
    }

    @GetMapping("/{initiativeId}")
    public InitiativeDto findById(@PathVariable Long initiativeId) {
        return initiativeService.findById(initiativeId);
    }
}