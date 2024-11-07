package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.filter.Filter;

import java.util.Objects;
import java.util.stream.Stream;

public class VacancyFilterById implements Filter<VacancyFilterDto, Vacancy> {
    @Override
    public boolean isApplicable(VacancyFilterDto filterDto) {
        return filterDto.getId() != null && filterDto.getId() > 0;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> itemStream, VacancyFilterDto filterDto) {
        return itemStream.filter(item -> Objects.equals(item.getId(), filterDto.getId()));
    }
}
