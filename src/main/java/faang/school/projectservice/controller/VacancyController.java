package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.NewVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vacancies")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public ResponseEntity<VacancyResponseDto> createVacancy(@Valid @RequestBody NewVacancyDto dto) {
        log.info("Received request to create new vacancy: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(dto));
    }
}
