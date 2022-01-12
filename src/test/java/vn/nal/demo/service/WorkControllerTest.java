package vn.nal.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import vn.nal.demo.DemoApplication;
import vn.nal.demo.enums.Status;
import vn.nal.demo.model.Work;
import vn.nal.demo.repository.WorkRepository;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WorkControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WorkRepository workRepository;

    @After
    public void resetDb() {
        workRepository.deleteAll();
    }

    @Test
    public void whenValidInput_thenCreateWork() throws Exception {
        Work mockWork = new Work(null, "test", LocalDate.of(2022,01,12), LocalDate.of(2022,02,12), Status.PLAINING);
        mvc.perform(post("/work").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(mockWork)));

        List<Work> found = workRepository.findAll();
        assertThat(found).extracting(Work::getWorkName).containsOnly("test");
    }

    @Test
    public void getWorkPagingThenReturnList() throws Exception {
        resetDb();

        List<Work> works = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            Work work = new Work(i, "Test " + i,
                    LocalDate.of(2022, 11, i), LocalDate.of(2022, 12, i),
                    i % 3 == 0 ? Status.PLAINING : (i % 3 == 1 ? Status.DOING : Status.COMPLETE));

            works.add(work);
        });

        workRepository.saveAll(works);

        mvc.perform(get("/work").queryParam("page", "1")
                .queryParam("size", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].workName", is("Test 4")))
                .andExpect(jsonPath("$[2].workName", is("Test 6")));

        mvc.perform(get("/work").queryParam("page", "1")
                .queryParam("size", "3")
                .queryParam("orderBy", "endingDate")
                .queryParam("orderType", "des"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].workName", is("Test 7")))
                .andExpect(jsonPath("$[2].workName", is("Test 5")));
    }

    @Test
    public void getWorkWhenFindById() throws Exception {
        Work work = createTestData(1);

        mvc.perform(get("/work/" + work.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("workName", is("test 1")))
                .andExpect(jsonPath("status", is("PLAINING")));
    }

    @Test
    public void deleteWorkReturnNoContent() throws Exception {
        Work work = createTestData(10);

        mvc.perform(delete("/work/" + work.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private Work createTestData(int index) {
        Work mockWork = new Work(index, "test " + index, LocalDate.of(2022,01,index), LocalDate.of(2022,02,index), Status.PLAINING);
        return workRepository.saveAndFlush(mockWork);
    }
}
