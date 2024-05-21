package clickme.clickme.ranking.ui;

import clickme.clickme.ranking.application.RankingService;
import clickme.clickme.ranking.ui.response.RankingResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RankingV1Controller.class)
@AutoConfigureMockMvc
class RankingV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService rankingService;

    @Test
    void findRankByName() throws Exception {
        String name = "seungpang";
        when(rankingService.findRankByName(isA(String.class))).thenReturn(1L);

        mockMvc.perform(get("/api/v1/rankings/" + name))
                .andExpect(status().isOk());
    }

    @Test
    void findLiveRanking() throws Exception {
        int startRank = 1;
        int endRank = 3;
        final List<RankingResponse> rankingResponses = List.of(
                new RankingResponse(1L, "seungpang", 5L, "image"),
                new RankingResponse(2L, "angie", 3L, "image"),
                new RankingResponse(3L, "chunsik", 1L, "image")
        );
        when(rankingService.findLiveRanking(isA(Integer.class), isA(Integer.class))).thenReturn(rankingResponses);

        ResultActions response =
                mockMvc.perform(get("/api/v1/rankings/live?startRank=" + startRank + "&endRank=" + endRank));

        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(rankingResponses.size())));
    }
}
