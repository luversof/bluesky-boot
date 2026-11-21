package io.github.luversof.boot.web;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.ModuleInfo;

@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebProperties extends AbstractBlueskyProperties<WebProperties> {

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.web";

  /** Whether to check unsupported browsers */
  private Boolean checkNotSupportedBrowser;

  /** Unsupported browser check pattern list */
  private List<String> notSupportedBrowserRegPatterns;

  /** Registering an exception list address pattern when checking for unsupported browser */
  private List<String> notSupportedBrowserExcludePathPatterns;

  public WebProperties() {}

  public Boolean getCheckNotSupportedBrowser() {
    return checkNotSupportedBrowser;
  }

  public void setCheckNotSupportedBrowser(Boolean checkNotSupportedBrowser) {
    this.checkNotSupportedBrowser = checkNotSupportedBrowser;
  }

  public List<String> getNotSupportedBrowserRegPatterns() {
    return notSupportedBrowserRegPatterns;
  }

  public void setNotSupportedBrowserRegPatterns(List<String> notSupportedBrowserRegPatterns) {
    this.notSupportedBrowserRegPatterns = notSupportedBrowserRegPatterns;
  }

  public List<String> getNotSupportedBrowserExcludePathPatterns() {
    return notSupportedBrowserExcludePathPatterns;
  }

  public void setNotSupportedBrowserExcludePathPatterns(
      List<String> notSupportedBrowserExcludePathPatterns) {
    this.notSupportedBrowserExcludePathPatterns = notSupportedBrowserExcludePathPatterns;
  }

  @Override
  public String toString() {
    return "WebProperties{"
        + "checkNotSupportedBrowser="
        + checkNotSupportedBrowser
        + ", notSupportedBrowserRegPatterns="
        + notSupportedBrowserRegPatterns
        + ", notSupportedBrowserExcludePathPatterns="
        + notSupportedBrowserExcludePathPatterns
        + '}';
  }

  @Override
  protected WebProperties presetOf(ModuleInfo moduleInfo) {
    return moduleInfo.getWebProperties();
  }

  @Override
  protected void applyDefaults() {
    if (checkNotSupportedBrowser == null) {
      checkNotSupportedBrowser = true;
    }
    if (notSupportedBrowserRegPatterns == null) {
      notSupportedBrowserRegPatterns = List.of(".*(MSIE [5-9]).*");
    }
    if (notSupportedBrowserExcludePathPatterns == null) {
      notSupportedBrowserExcludePathPatterns =
          List.of(
              "/css/**",
              "/html/**",
              "/js/**",
              "/img/**",
              "/message/**",
              "/favicon.ico",
              "/monitor/**",
              "/support/**",
              "/error/**");
    }
  }
}
