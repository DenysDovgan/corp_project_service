package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageNameFilter implements StageFilter {

    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getStageName()!=null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> stage.getStageName().equals(stageFilterDto.getStageName()));
    }
}
