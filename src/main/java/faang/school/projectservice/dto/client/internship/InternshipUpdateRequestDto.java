package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InternshipUpdateRequestDto {

    private Long id;
    private List<Long> idsOfUsersWithCompletedTasks;
    private TeamRole internNewTeamRole;
    private InternshipStatus internshipStatus;
}
