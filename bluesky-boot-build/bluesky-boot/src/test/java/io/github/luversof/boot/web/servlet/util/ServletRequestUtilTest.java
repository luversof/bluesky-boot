package io.github.luversof.boot.web.servlet.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

/** 매핑이 요청에 남긴 경로 변수를 그대로 돌려준다. 요청 밖에서는 빈 맵이다. */
class ServletRequestUtilTest {

  @Test
  void returnsPathVariablesOfMatchedMapping() throws Exception {
    MockMvcBuilders.standaloneSetup(new ItemController())
        .build()
        .perform(get("/items/7/parts/wheel"))
        .andExpect(content().string("{id=7, part=wheel}"));
  }

  @Test
  void emptyOutsideRequest() {
    RequestContextHolder.resetRequestAttributes();
    assertThat(ServletRequestUtil.getUriVariableMap()).isEmpty();
  }

  @RestController
  static class ItemController {

    @GetMapping({"/items/{id}", "/items/{id}/parts/{part}"})
    String item() {
      Map<String, String> variables = ServletRequestUtil.getUriVariableMap();
      return new TreeMap<>(variables).toString();
    }
  }
}
