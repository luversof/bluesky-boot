package io.github.luversof.boot.autoconfigure.web.servlet.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.github.luversof.boot.autoconfigure.web.util.ExceptionUtil;
import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.core.CoreBaseProperties;

/** 화면/JSON 응답은 예외가 난 핸들러로 고른다. 예외 처리기는 그 핸들러를 매핑이 요청에 남긴 값에서 읽는다(예전에는 예외마다 매핑 전체로 다시 찾았다). */
class CoreMvcExceptionHandlerTests {

  private static final String ERROR_VIEW = "error-view";

  private ApplicationContext previousContext;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    previousContext = ApplicationContextUtil.getApplicationContext();
    var context = new GenericApplicationContext();
    context.getBeanFactory().registerSingleton("coreBaseProperties", new CoreBaseProperties());
    context
        .getBeanFactory()
        .registerSingleton("contentNegotiationManager", new ContentNegotiationManager());
    context
        .getBeanFactory()
        .registerSingleton(
            "messageSourceAccessor", new MessageSourceAccessor(new StaticMessageSource()));
    context.refresh();
    ApplicationContextUtil.setApplicationContext(context);
    ErrorViewResolver errorViewResolver =
        (request, status, model) -> new ModelAndView(ERROR_VIEW, model, status);
    ExceptionUtil.setErrorViewResolverList(List.of(errorViewResolver));

    mockMvc =
        MockMvcBuilders.standaloneSetup(new FailingController())
            .setControllerAdvice(new CoreMvcExceptionHandler())
            .build();
  }

  @AfterEach
  void tearDown() {
    ApplicationContextUtil.setApplicationContext(previousContext);
    ExceptionUtil.setErrorViewResolverList(null);
  }

  @Test
  void jsonHandlerGetsProblemDetail() throws Exception {
    mockMvc
        .perform(get("/json").accept(MediaType.ALL))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void pageHandlerGetsErrorView() throws Exception {
    mockMvc
        .perform(get("/page").accept(MediaType.ALL))
        .andExpect(status().isInternalServerError())
        .andExpect(forwardedUrl(ERROR_VIEW));
  }

  @Test
  void requestWithoutHandlerStillGetsResponse() throws Exception {
    mockMvc
        .perform(post("/json").accept(MediaType.TEXT_HTML))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(forwardedUrl(ERROR_VIEW));
  }

  @RestController
  static class FailingController {

    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    String json() {
      throw new IllegalStateException("json handler failed");
    }

    @GetMapping("/page")
    String page() {
      throw new IllegalStateException("page handler failed");
    }
  }
}
