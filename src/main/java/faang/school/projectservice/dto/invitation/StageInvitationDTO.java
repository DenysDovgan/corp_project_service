package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageInvitationDTO {

    private Long id;
    @NotNull(message = "Идентификатор этапа не может быть null")
    private Long stageId;
    @NotNull(message = "Идентификатор автора не может быть null")
    private Long authorId;
    @NotNull(message = "Идентификатор приглашенного не может быть null")
    private Long inviteeId;
    private StageInvitationStatus status;
    @NotBlank(message = "Причина отклонения обязательна")
    @Size(max = 255, message = "Причина отклонения слишком длинная")
    private String rejectionReason;
}