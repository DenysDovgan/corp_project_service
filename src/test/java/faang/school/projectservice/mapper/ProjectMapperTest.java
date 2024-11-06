package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProjectMapperTest {

    private final ProjectMapper mapper = new ProjectMapperImpl();

    @Test
    @DisplayName("Test creation dto to entity mapping")
    public void toEntityTest() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .ownerId(1L)
                .parentId(2L)
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Project result = mapper.toEntity(dto);

        assertEquals(dto.getOwnerId(), result.getOwnerId());
        assertNull(result.getParentProject());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getVisibility(), result.getVisibility());
    }
}
