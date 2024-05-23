package clickme.clickme.history.ui;

import clickme.clickme.history.application.ClickCountHistoryService;
import clickme.clickme.history.application.dto.ClickCountHistoriesResponse;
import clickme.clickme.history.application.dto.ClickCountHistoryResponse;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(DailyClickCountV1Controller.class)
@AutoConfigureMockMvc
class DailyClickCountV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClickCountHistoryService clickCountHistoryService;

    @Test
    void findClickCountHistoryByNameAndDateBetween() throws Exception {
        ClickCountHistoriesResponse clickCountHistoryResponses = new ClickCountHistoriesResponse(List.of(
                new ClickCountHistoryResponse(LocalDate.parse("2023-12-04"), 4L),
                new ClickCountHistoryResponse(LocalDate.parse("2023-12-05"), 3L),
                new ClickCountHistoryResponse(LocalDate.parse("2023-12-06"), 2L),
                new ClickCountHistoryResponse(LocalDate.parse("2023-12-07"), 5L),
                new ClickCountHistoryResponse(LocalDate.parse("2023-12-08"), 6L),
                new ClickCountHistoryResponse(LocalDate.parse("2023-12-09"), 1L),
                new ClickCountHistoryResponse(LocalDate.parse("2023-12-10"), 2L)
        ));
        when(clickCountHistoryService.findClickCountHistoryByNameAndDateBetween(isA(String.class)))
                .thenReturn(clickCountHistoryResponses);

        String name = "seungpang";
        final ResultActions response = mockMvc.perform(get("/api/v1/daily-click-count/" + name));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.clickCountHistories.size()",
                        CoreMatchers.is(clickCountHistoryResponses.clickCountHistories().size())));
    }
}
