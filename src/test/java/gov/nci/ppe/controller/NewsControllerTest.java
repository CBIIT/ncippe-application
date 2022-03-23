package gov.nci.ppe.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import gov.nci.ppe.constants.UrlConstants;

@ActiveProfiles("unittest")
@WebMvcTest(controllers = NewsController.class)
public class NewsControllerTest {

    @Autowired
	private MockMvc mockMvc;

    
    @Test
    public void testGetNewsEvents() throws Exception{
        String result = mockMvc.perform(get(UrlConstants.URL_NEWS_EVENTS)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(StringUtils.isNotBlank(result));
        
    }



    
}
