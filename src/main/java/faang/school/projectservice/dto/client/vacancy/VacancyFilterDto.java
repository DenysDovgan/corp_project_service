package faang.school.projectservice.dto.client.vacancy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyFilterDto {
    private String name;
    private Long projectId;
}