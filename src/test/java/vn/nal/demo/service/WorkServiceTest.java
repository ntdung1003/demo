package vn.nal.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import vn.nal.demo.enums.Status;
import vn.nal.demo.model.Work;
import vn.nal.demo.repository.WorkRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class WorkServiceTest {

    @Mock
    WorkRepository workRepository;

    @InjectMocks
    WorkService workService;


    @Test
    void whenSaveSuccessful_shouldReturnObject() {
        Work mockWork = new Work(1, "test", LocalDate.of(2022,01,12), LocalDate.of(2022,02,12), Status.PLAINING);

        when(workRepository.save(mockWork)).thenReturn(mockWork);

        Work actualWork = workService.save(mockWork);
        assertThat(actualWork.getId()).isEqualTo(mockWork.getId());

        mockWork.setWorkName("testEdit");
        actualWork = workService.save(mockWork);

        assertThat(actualWork.getWorkName()).isEqualTo(mockWork.getWorkName());
    }

    @Test
    void whenFindById_shouldReturnObject(){
        Work mockWork = new Work(1, "test", LocalDate.of(2022,01,12), LocalDate.of(2022,02,12), Status.PLAINING);
        when(workRepository.findById(1)).thenReturn(Optional.of(mockWork));
        when(workRepository.findById(7)).thenReturn(Optional.of(new Work()));

        Work actualWork = workService.findById(1);
        assertThat(actualWork.getId()).isEqualTo(1);
        assertThat(actualWork.getWorkName()).isEqualTo("test");

        actualWork = workService.findById(7);
        assertThat(actualWork.getId()).isNull();
        assertThat(actualWork.getWorkName()).isNull();
    }

    @Test
    void whenDeleteById_shouldDeleteObject(){

        workService.delete(1);

        Work actualWork = workService.findById(1);
        assertThat(actualWork.getId()).isNull();
    }

    @Test
    void whenGetList_shouldReturnList() {
        List<Work> works = new ArrayList<>();
        IntStream.range(1, 4).forEach(i -> {
            Work work = new Work(i, "Test " + i,
                    LocalDate.of(2022, 11, i), LocalDate.of(2022, 12, i),
                    i % 3 == 0 ? Status.PLAINING : (i % 3 == 1 ? Status.DOING : Status.COMPLETE));

            works.add(work);
        });

        when(workRepository.findAll(PageRequest.of(0,3))).thenReturn(new PageImpl<>(works));
        when(workRepository.findAll(PageRequest.of(0,3, Sort.by("workName")))).thenReturn(new PageImpl<>(works));

        List<Work> actualWorks = workService.getList(0, 3, null, null).toList();
        assertThat(actualWorks.size()).isEqualTo(3);
        assertThat(actualWorks.get(0).getId()).isEqualTo(1);

        actualWorks = workService.getList(0, 3, "workName", null).toList();
        assertThat(actualWorks.size()).isEqualTo(3);
        assertThat(actualWorks.get(0).getId()).isEqualTo(1);

        works.sort(Comparator.comparing(Work::getStartingDate).reversed());
        when(workRepository.findAll(PageRequest.of(0,3, Sort.by("endingDate").descending()))).thenReturn(new PageImpl<>(works));

        actualWorks = workService.getList(0, 3, "endingDate", "des").toList();
        assertThat(actualWorks.size()).isEqualTo(3);
        assertThat(actualWorks.get(0).getId()).isEqualTo(3);

    }
}
