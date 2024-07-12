package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MomentDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return Objects.nonNull(filters.getStartDate()) &&
                Objects.nonNull(filters.getEndDate());
    }

    @Override
    public Stream<Moment> apply(Supplier<Stream<Moment>> moments, MomentFilterDto filters) {
        return moments.get().filter(moment -> filters.getStartDate().isAfter(moment.getDate()) &&
                filters.getEndDate().isBefore(moment.getDate()));
    }
}