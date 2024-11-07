package faang.school.projectservice.dto.invitation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDTO {
    private Long id;
    private Long stageId;
    private Long authorId;
    private Long inviteeId;
    private String status;
    private String rejectionReason;
}
