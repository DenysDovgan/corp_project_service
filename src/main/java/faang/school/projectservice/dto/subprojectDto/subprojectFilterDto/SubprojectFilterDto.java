package faang.school.projectservice.dto.subprojectDto.subprojectFilterDto;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubprojectFilterDto {
    @NotNull
    private String name;
    @NotNull
    private ProjectStatus status;
}
