package faang.school.projectservice.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseJiraDto {
    private Long id;
    private String username;
    private String projectKey;
    private String projectUrl;
    private Long projectId;
}
