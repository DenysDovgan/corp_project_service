package faang.school.projectservice.mappers.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {

    @Mapping(source = "stage.id", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "inviteeId")
    StageInvitationDTO toDto(StageInvitation stageInvitation);

    @Mapping(source = "stageId", target = "stage.id")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "inviteeId", target = "invited.id")
    StageInvitation toEntity(StageInvitationDTO stageInvitationDTO);
}