package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VacancyDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private long projectId;
    private List<Long> candidateIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long createdBy;
    private long updatedBy;
    private VacancyStatus status;
    @PositiveOrZero
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;




}
