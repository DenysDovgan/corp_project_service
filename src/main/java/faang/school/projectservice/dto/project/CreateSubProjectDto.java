package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Valid
public class CreateSubProjectDto {
    @NotNull
    private String name;
    private String description;
    private ProjectVisibility visibility;
}
