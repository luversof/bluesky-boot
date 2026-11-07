package io.github.luversof.boot.test.autoconfigure.restdocs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import tools.jackson.databind.json.JsonMapper;

class RestDocsTestMockListTest {

  record Item(String name) {}

  @TempDir Path mockResponsePath;

  @Test
  void getMockListConvertsElementsToValueType() throws IOException {
    Files.writeString(mockResponsePath.resolve("items.json"), "[{\"name\":\"a\"}]");
    var restDocsTest =
        new RestDocsTest() {
          @Override
          protected String getMockResponsePath() {
            return mockResponsePath + "/";
          }
        };
    ReflectionTestUtils.setField(restDocsTest, "jsonMapper", new JsonMapper());

    assertThat(restDocsTest.getMockList("items.json", Item.class)).containsExactly(new Item("a"));
  }
}
