package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public interface StageFilter {
    boolean isApplicable(StageFilterDto stageDto);

    Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto);
}
