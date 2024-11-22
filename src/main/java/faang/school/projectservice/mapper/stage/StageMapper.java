package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageRolesMapper.class)
public interface StageMapper {

    Stage toEntity(StageDto stageDto);

    @Mapping(source = "project.id", target = "projectId")
    StageDto toDto(Stage stage);

    List<StageDto> toDto(List<Stage> stages);
}