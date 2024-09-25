package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.validation.CreateGroup;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated(CreateGroup.class)
public interface StageInvitationService {

    StageInvitationDto sendInvitation(StageInvitationDto invitationDto);

    StageInvitationDto acceptInvitation(@Positive Long id);

    StageInvitationDto declineInvitation(@Positive Long id, String reason);

    List<StageInvitationDto> getInvitations(StageInvitationFilterDto filterDto);
}