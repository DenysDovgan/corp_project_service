package faang.school.projectservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long createdBy;
    private Long count;
}