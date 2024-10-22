package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.model.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies")
@Validated
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto create(@Valid @NotNull @RequestBody VacancyDto dto) {
        return vacancyService.create(dto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto update(@Valid @NotNull @RequestBody VacancyDto dto) {
        return vacancyService.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Positive @NotNull Long id) {
        vacancyService.delete(id);
    }

    @PostMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<VacancyDto> findAll(@Valid @NotNull @RequestBody VacancyFilterDto filter) {
        return vacancyService.findAll(filter);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto findById(@PathVariable("id") @Positive @NotNull Long id) {
        return vacancyService.findById(id);
    }
}