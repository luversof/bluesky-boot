package io.github.luversof.boot.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.ModuleInfo;

@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainProperties extends AbstractBlueskyProperties<DomainProperties> {

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.web.domain";

  /**
   * When using multi-modules, if you make an add-path-pattern declaration, you need an
   * addPathPatterns declaration for each module.
   *
   * <p>Registering the AntPathMatcher Pattern
   */
  private List<String> addPathPatterns;

  /**
   * If the site address is multi, use.
   *
   * <p>If your site doesn't have a PC web/mobile web distinction, declare only WEB and use it.
   */
  private List<URI> webUrls;

  /**
   * Use the mobile web address for your site if it is multi-use
   *
   * <p>Mobile web address must be declared if there is a PC web/mobile web distinction
   *
   * <p>Used separately in unified notification processing
   */
  private List<URI> mobileWebUrls;

  /**
   * When declared and used by domain in a multi-module project, the domain is a variable for local
   * development that is set aside for development purposes.
   *
   * <p>You can use development domains in the form of a list by declaring them in succession with
   * ",".
   *
   * <p>ex) devDomains=http://local.a.com,http://local.b.com
   */
  private List<URI> devDomains;

  /** List of static paths (not redirect targets) */
  private List<String> staticPaths;

  /** List of exception paths (list that are not redirect targets) */
  private List<String> excludePaths;

  /**
   * Set the request root path
   *
   * <p>Default is "/" (full)
   */
  private String requestPath;

  /** Set the forward root path */
  private String forwardPath;

  public DomainProperties() {}

  public List<String> getAddPathPatterns() {
    return addPathPatterns;
  }

  public void setAddPathPatterns(List<String> addPathPatterns) {
    this.addPathPatterns = addPathPatterns;
  }

  public List<URI> getWebUrls() {
    return webUrls;
  }

  public void setWebUrls(List<URI> webUrls) {
    this.webUrls = webUrls;
  }

  public List<URI> getMobileWebUrls() {
    return mobileWebUrls;
  }

  public void setMobileWebUrls(List<URI> mobileWebUrls) {
    this.mobileWebUrls = mobileWebUrls;
  }

  public List<URI> getDevDomains() {
    return devDomains;
  }

  public void setDevDomains(List<URI> devDomains) {
    this.devDomains = devDomains;
  }

  public List<String> getStaticPaths() {
    return staticPaths;
  }

  public void setStaticPaths(List<String> staticPaths) {
    this.staticPaths = staticPaths;
  }

  public List<String> getExcludePaths() {
    return excludePaths;
  }

  public void setExcludePaths(List<String> excludePaths) {
    this.excludePaths = excludePaths;
  }

  public String getRequestPath() {
    return requestPath;
  }

  public void setRequestPath(String requestPath) {
    this.requestPath = requestPath;
  }

  public String getForwardPath() {
    return forwardPath;
  }

  public void setForwardPath(String forwardPath) {
    this.forwardPath = forwardPath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass() || !super.equals(o)) {
      return false;
    }
    DomainProperties that = (DomainProperties) o;
    return Objects.equals(addPathPatterns, that.addPathPatterns)
        && Objects.equals(webUrls, that.webUrls)
        && Objects.equals(mobileWebUrls, that.mobileWebUrls)
        && Objects.equals(devDomains, that.devDomains)
        && Objects.equals(staticPaths, that.staticPaths)
        && Objects.equals(excludePaths, that.excludePaths)
        && Objects.equals(requestPath, that.requestPath)
        && Objects.equals(forwardPath, that.forwardPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        addPathPatterns,
        webUrls,
        mobileWebUrls,
        devDomains,
        staticPaths,
        excludePaths,
        requestPath,
        forwardPath);
  }

  @Override
  public String toString() {
    return "DomainProperties{"
        + "addPathPatterns="
        + addPathPatterns
        + ", webUrls="
        + webUrls
        + ", mobileWebUrls="
        + mobileWebUrls
        + ", devDomains="
        + devDomains
        + ", staticPaths="
        + staticPaths
        + ", excludePaths="
        + excludePaths
        + ", requestPath='"
        + requestPath
        + '\''
        + ", forwardPath='"
        + forwardPath
        + '\''
        + '}';
  }

  /**
   * The address of the site
   *
   * <p>If your site doesn't have a PC web/mobile web distinction, declare only WEB and use the
   */
  public void setWeb(URI uri) {
    if (this.webUrls == null) {
      this.webUrls = new ArrayList<>();
    }
    if (this.webUrls.contains(uri)) {
      return;
    }
    this.webUrls.add(uri);
  }

  /**
   * Mobile web address for your site
   *
   * <p>Mobile web address must be declared if there is a PC web/mobile web distinction
   */
  public void setMobileWeb(URI uri) {
    if (this.mobileWebUrls == null) {
      this.mobileWebUrls = new ArrayList<>();
    }
    if (this.mobileWebUrls.contains(uri)) {
      return;
    }
    this.mobileWebUrls.add(uri);
  }

  public URI getWeb() {
    // 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
    return webUrls == null || webUrls.isEmpty() ? null : webUrls.get(0);
  }

  public URI getMobileWeb() {
    // 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
    return mobileWebUrls == null || mobileWebUrls.isEmpty() ? getWeb() : mobileWebUrls.get(0);
  }

  @Override
  protected DomainProperties presetOf(ModuleInfo moduleInfo) {
    return moduleInfo.getDomainProperties();
  }

  @Override
  protected void applyDefaults() {
    if (addPathPatterns == null) {
      addPathPatterns = new ArrayList<>();
    }
    if (webUrls == null) {
      webUrls = new ArrayList<>();
    }
    if (mobileWebUrls == null) {
      mobileWebUrls = new ArrayList<>();
    }
    if (devDomains == null) {
      devDomains = new ArrayList<>();
    }
    if (staticPaths == null) {
      staticPaths =
          new ArrayList<>(
              List.of(
                  "/css/",
                  "/html/",
                  "/js/",
                  "/img/",
                  "/message/",
                  "/favicon.ico",
                  "/monitor/",
                  "/support/"));
    }
    if (excludePaths == null) {
      excludePaths = new ArrayList<>(List.of("/UiDev/", "/_check"));
    }
    if (requestPath == null) {
      requestPath = "/";
    }
    if (forwardPath == null) {
      forwardPath = "/";
    }
  }
}
