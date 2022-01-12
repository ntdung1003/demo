package vn.nal.demo.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import vn.nal.demo.enums.Status;
import vn.nal.demo.model.Work;
import vn.nal.demo.repository.WorkRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WorkRepositoryTest {

    @Autowired
    private WorkRepository workRepository;

    @After
    public void resetDb() {
        workRepository.deleteAll();
    }

    @Before
    public void initDB() {
        List<Work> works = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            Work work = new Work(i, "Test " + i,
                    LocalDate.of(2022, 11, i), LocalDate.of(2022, 12, i),
                    i % 3 == 0 ? Status.PLAINING : (i % 3 == 1 ? Status.DOING : Status.COMPLETE));

            works.add(work);
        });

        workRepository.saveAll(works);
    }

    @Test
    public void whenGetListThenReturnWithOrder() {
        List<Work> works = workRepository.findAll();
        assertThat(works.size()).isEqualTo(10);

        works = workRepository.findAll(PageRequest.of(0,3)).toList();
        assertThat(works.size()).isEqualTo(3);

        works = workRepository.findAll(PageRequest.of(1,3)).toList();
        assertThat(works.get(0).getWorkName()).isEqualTo("Test 4");

        works = workRepository.findAll(PageRequest.of(1,3)).toList();
        assertThat(works.get(0).getWorkName()).isEqualTo("Test 4");

        works = workRepository.findAll(PageRequest.of(1,3, Sort.by("endingDate").descending())).toList();
        assertThat(works.get(0).getWorkName()).isEqualTo("Test 7");
    }

    @Test
    public void whenFindByIdThenReturnObject() {
        Work mockWork = new Work(null, "test", LocalDate.of(2022,01,12), LocalDate.of(2022,02,12), Status.PLAINING);
        mockWork = workRepository.save(mockWork);

        boolean foudWork = workRepository.findById(mockWork.getId()).isPresent();
        assertThat(foudWork).isTrue();

        Work work = workRepository.findById(mockWork.getId()).get();
        assertThat(work.getId()).isEqualTo(mockWork.getId());
    }

    @Test
    public void whenSaveThenReturnObject() {
        Work mockWork = new Work(null, "test2", LocalDate.of(2022,01,12), LocalDate.of(2022,02,12), Status.PLAINING);
        mockWork = workRepository.save(mockWork);

        assertThat(mockWork.getWorkName()).isEqualTo("test2");
        assertThat(mockWork.getId()).isNotNull();
    }

    @Test
    public void whenDeleteThenNotFoundObject() {
        Work mockWork = new Work(null, "test3", LocalDate.of(2022,01,12), LocalDate.of(2022,02,12), Status.PLAINING);
        mockWork = workRepository.save(mockWork);

        assertThat(mockWork.getWorkName()).isEqualTo("test3");
        assertThat(mockWork.getId()).isNotNull();

        workRepository.deleteById(mockWork.getId());
        boolean isPresent = workRepository.findById(mockWork.getId()).isPresent();

        assertThat(isPresent).isFalse();
    }
}
