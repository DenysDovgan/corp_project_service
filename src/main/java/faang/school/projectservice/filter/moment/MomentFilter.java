package faang.school.projectservice.filter.moment;


import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filters);

    Stream<Moment> apply(Supplier<Stream<Moment>> moments, MomentFilterDto filters);
}