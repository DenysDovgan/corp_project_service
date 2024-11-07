package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    VacancyDto dto;

    @BeforeEach
    void setUp() {
        dto = createTestVacancyDto();
    }

    @Test
    @DisplayName("Create a new vacancy successfully")
    void testCreateVacancySuccess() {
        when(vacancyService.create(dto)).thenReturn(dto);

        ResponseEntity<VacancyDto> resultResponse = vacancyController.createVacancy(dto);
        VacancyDto resultDto = resultResponse.getBody();

        verify(vacancyService).create(dto);

        assertNotNull(resultResponse);
        assertEquals(dto, resultDto);
        assertEquals(HttpStatus.CREATED, resultResponse.getStatusCode());
        assertEquals("Vacancy 1", resultDto.getName());
    }

    private VacancyDto createTestVacancyDto() {
        return VacancyDto.builder()
                .id(1L)
                .name("Vacancy 1")
                .description("Vacancy 1 description")
                .projectId(1L)
                .createdAt(LocalDateTime.now())
                .salary(100.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L))
                .build();
    }
}
