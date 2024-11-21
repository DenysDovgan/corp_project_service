package faang.school.projectservice.mapper.stageInvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.model.stageinvitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    StageInvitation toEntity(StageInvitationDto dto);

    @Mapping(source = "stage.stageId",target = "stageId")
    @Mapping(source = "author.id",target = "authorId")
    @Mapping(source = "invited.id",target = "invitedId")
    StageInvitationDto toDto(StageInvitation entity);
}