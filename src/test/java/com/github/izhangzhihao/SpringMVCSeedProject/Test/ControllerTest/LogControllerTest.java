package com.github.izhangzhihao.SpringMVCSeedProject.Test.ControllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.izhangzhihao.SpringMVCSeedProject.Test.TestUtils.BaseTest;
import com.github.izhangzhihao.SpringMVCSeedProject.Utils.PageResults;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LogControllerTest extends BaseTest {
   /*@Autowired
    private LogController logController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(logController)
                .setViewResolvers(viewResolver)
                .build();
    }*/


    /**
     * 日志页面测试
     */
    @Test
    public void logPageTest() throws Exception {
        mockMvc.perform(get("/Log/"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(view().name("Log/Log"))
                .andExpect(forwardedUrl("/WEB-INF/views/Log/Log.jsp"));
    }

    /**
     * 返回日志信息测试
     */
    @Test
    public void getLogInfoTest() throws Exception {
        String contentAsString = mockMvc
                .perform(get("/Log/LogInfo"))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(contentAsString);
        //将string转为json
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(contentAsString, Map.class);
        System.out.println(map);
        //断言相等
        assertEquals(Integer.parseInt(map.get("LogUtilsCount").toString())
                        + Integer.parseInt(map.get("LogAspectCount").toString())
                        + Integer.parseInt(map.get("otherCount").toString())
                , map.get("totalCount"));
    }

    /**
     * 日志分页测试
     */
    @Test
    public void getLogByPageTest() throws Exception {
        //自动将json转为实体，避免了手动转换，缺点是不可以测试http status
        /*PageResults pageResults = restTemplate
                .getForObject("/Log/getLogByPage/pageNumber/" + 2 + "/pageSize/" + 10,
                        PageResults.class);*/

        String contentAsString = mockMvc
                .perform(
                        get("/Log/LogByPage/pageNumber/" + 2 + "/pageSize/" + 10)
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(contentAsString);
        //手动将string转为json
        ObjectMapper objectMapper = new ObjectMapper();
        PageResults pageResults = objectMapper.readValue(contentAsString, PageResults.class);


        /*Map map = objectMapper.readValue(contentAsString, Map.class);
        System.out.println(map);
        int previousPage = Integer.parseInt(map.get("previousPage").toString());
        int currentPage = Integer.parseInt(map.get("currentPage").toString());
        int nextPage = Integer.parseInt(map.get("nextPage").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        int totalCount = Integer.parseInt(map.get("totalCount").toString());
        int pageCount = Integer.parseInt(map.get("pageCount").toString());
        assertTrue(previousPage <= currentPage);
        assertTrue(currentPage <= nextPage);
        assertTrue(pageSize * pageCount >= totalCount);

        int i = totalCount % pageSize;
        if (i == 0) {
            assertEquals(totalCount / pageSize, pageCount);
        } else {
            assertEquals(totalCount / pageSize + 1, pageCount);
        }*/


        int previousPage = pageResults.getPreviousPage();
        int currentPage = pageResults.getCurrentPage();
        int nextPage = pageResults.getNextPage();
        int pageSize = pageResults.getPageSize();
        int totalCount = pageResults.getTotalCount();
        int pageCount = pageResults.getPageCount();

        assertTrue(previousPage <= currentPage);
        assertTrue(currentPage <= nextPage);
        assertTrue(pageSize * pageCount >= totalCount);

        int i = totalCount % pageSize;
        if (i == 0) {
            assertEquals(totalCount / pageSize, pageCount);
        } else {
            assertEquals(totalCount / pageSize + 1, pageCount);
        }
    }
}
