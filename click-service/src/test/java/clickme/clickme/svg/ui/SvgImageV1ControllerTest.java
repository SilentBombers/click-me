package clickme.clickme.svg.ui;

import clickme.clickme.svg.application.SvgImageService;
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

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SvgImageV1Controller.class)
@AutoConfigureMockMvc
class SvgImageV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SvgImageService svgImageService;

    @Test
    void getNonClickableSvgImageByName() throws Exception {
        String name = "seungpang";
        String svgUrl = "null";
        String expected = "seungpang click count";
        when(svgImageService.generateNonClickableSvgImage(isA(String.class), isA(String.class)))
                .thenReturn(expected);

        final ResultActions response = mockMvc.perform(get("/api/v1/svg-image?name=" + name + "&svgUrl=" + svgUrl));

        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$", CoreMatchers.is(expected)));
    }

    @Test
    void getClickableSvgImageByName() throws Exception {
        String name = "seungpang";
        String expected = "seungpang click count";
        when(svgImageService.generateClickableSvgImage(isA(String.class)))
                .thenReturn(expected);

        final ResultActions response = mockMvc.perform(get("/api/v1/svg-image/increment?name=" + name));

        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$", CoreMatchers.is(expected)));
    }
}
