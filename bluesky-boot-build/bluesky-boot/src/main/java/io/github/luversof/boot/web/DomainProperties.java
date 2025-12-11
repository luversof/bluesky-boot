package io.github.luversof.boot.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;
import java.util.Objects;

@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainProperties
		extends AbstractBlueskyProperties<DomainProperties, DomainProperties.DomainPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	public static final String PREFIX = "bluesky-boot.web.domain";

	/**
	 * When using multi-modules, if you make an add-path-pattern declaration, you
	 * need an addPathPatterns declaration for each module.
	 * 
	 * Registering the AntPathMatcher Pattern
	 */
	private List<String> addPathPatternList = new ArrayList<>();

	/**
	 * If the site address is multi, use.
	 * 
	 * If your site doesn't have a PC web/mobile web distinction, declare only WEB
	 * and use it.
	 */
	private List<URI> webList = new ArrayList<>();

	/**
	 * Use the mobile web address for your site if it is multi-use
	 * 
	 * Mobile web address must be declared if there is a PC web/mobile web
	 * distinction
	 * 
	 * Used separately in unified notification processing
	 */
	private List<URI> mobileWebList = new ArrayList<>();

	/**
	 * When declared and used by domain in a multi-module project, the domain is a
	 * variable for local development that is set aside for development purposes.
	 * 
	 * You can use development domains in the form of a list by declaring them in
	 * succession with ",".
	 * 
	 * ex) devDomainList=http://local.a.com,http://local.b.com
	 */
	private List<URI> devDomainList = new ArrayList<>();

	/**
	 * List of static paths (not redirect targets)
	 */
	private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/", "/favicon.ico",
			"/monitor/", "/support/");

	/**
	 * List of exception paths (list that are not redirect targets)
	 */
	private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");

	/**
	 * Set the request root path
	 * 
	 * Default is "/" (full)
	 */
	private String requestPath = "/";

	/**
	 * Set the forward root path
	 */
	private String forwardPath = "/";

	public DomainProperties() {
	}

	public DomainProperties(List<String> addPathPatternList, List<URI> webList, List<URI> mobileWebList,
			List<URI> devDomainList, List<String> staticPathList, List<String> excludePathList, String requestPath,
			String forwardPath) {
		this.addPathPatternList = addPathPatternList;
		this.webList = webList;
		this.mobileWebList = mobileWebList;
		this.devDomainList = devDomainList;
		this.staticPathList = staticPathList;
		this.excludePathList = excludePathList;
		this.requestPath = requestPath;
		this.forwardPath = forwardPath;
	}

	public List<String> getAddPathPatternList() {
		return addPathPatternList;
	}

	public void setAddPathPatternList(List<String> addPathPatternList) {
		this.addPathPatternList = addPathPatternList;
	}

	public List<URI> getWebList() {
		return webList;
	}

	public void setWebList(List<URI> webList) {
		this.webList = webList;
	}

	public List<URI> getMobileWebList() {
		return mobileWebList;
	}

	public void setMobileWebList(List<URI> mobileWebList) {
		this.mobileWebList = mobileWebList;
	}

	public List<URI> getDevDomainList() {
		return devDomainList;
	}

	public void setDevDomainList(List<URI> devDomainList) {
		this.devDomainList = devDomainList;
	}

	public List<String> getStaticPathList() {
		return staticPathList;
	}

	public void setStaticPathList(List<String> staticPathList) {
		this.staticPathList = staticPathList;
	}

	public List<String> getExcludePathList() {
		return excludePathList;
	}

	public void setExcludePathList(List<String> excludePathList) {
		this.excludePathList = excludePathList;
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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		DomainProperties that = (DomainProperties) o;
		return Objects.equals(addPathPatternList, that.addPathPatternList) && Objects.equals(webList, that.webList)
				&& Objects.equals(mobileWebList, that.mobileWebList)
				&& Objects.equals(devDomainList, that.devDomainList)
				&& Objects.equals(staticPathList, that.staticPathList)
				&& Objects.equals(excludePathList, that.excludePathList)
				&& Objects.equals(requestPath, that.requestPath) && Objects.equals(forwardPath, that.forwardPath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), addPathPatternList, webList, mobileWebList, devDomainList, staticPathList,
				excludePathList, requestPath, forwardPath);
	}

	@Override
	public String toString() {
		return "DomainProperties{" +
				"addPathPatternList=" + addPathPatternList +
				", webList=" + webList +
				", mobileWebList=" + mobileWebList +
				", devDomainList=" + devDomainList +
				", staticPathList=" + staticPathList +
				", excludePathList=" + excludePathList +
				", requestPath='" + requestPath + '\'' +
				", forwardPath='" + forwardPath + '\'' +
				'}';
	}

	/**
	 * The address of the site
	 * 
	 * If your site doesn't have a PC web/mobile web distinction, declare only WEB
	 * and use the
	 */
	public void setWeb(URI uri) {
		if (this.webList.contains(uri)) {
			return;
		}
		this.webList.add(uri);
	}

	/**
	 * Mobile web address for your site
	 * 
	 * Mobile web address must be declared if there is a PC web/mobile web
	 * distinction
	 */
	public void setMobileWeb(URI uri) {
		if (this.mobileWebList.contains(uri)) {
			return;
		}
		this.mobileWebList.add(uri);
	}

	public URI getWeb() {
		// 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
		return this.getWebList().isEmpty() ? null : this.getWebList().get(0);
	}

	public URI getMobileWeb() {
		// 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
		return mobileWebList.isEmpty() ? getWeb() : mobileWebList.get(0);
	}

	protected BiConsumer<DomainProperties, DomainPropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			if (properties == null) {
				return;
			}
			var propertyMapper = PropertyMapper.get();
			propertyMapper.from(properties::getAddPathPatternList).to(builder::addPathPatternList);
			propertyMapper.from(properties::getWebList).whenNot(x -> x == null || x.isEmpty()).to(builder::webList);
			propertyMapper.from(properties::getMobileWebList).whenNot(x -> x == null || x.isEmpty())
					.to(builder::mobileWebList);
			propertyMapper.from(properties::getDevDomainList).whenNot(x -> x == null || x.isEmpty())
					.to(builder::devDomainList);
			propertyMapper.from(properties::getStaticPathList).whenNot(x -> x == null || x.isEmpty())
					.to(builder::staticPathList);
			propertyMapper.from(properties::getExcludePathList).whenNot(x -> x == null || x.isEmpty())
					.to(builder::excludePathList);
			propertyMapper.from(properties::getRequestPath).to(builder::requestPath);
			propertyMapper.from(properties::getForwardPath).to(builder::forwardPath);
		};
	}

	@Override
	protected DomainPropertiesBuilder getBuilder() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		return parentModuleInfo == null ? DomainProperties.builder() : parentModuleInfo.getDomainPropertiesBuilder();
	}

	public static DomainPropertiesBuilder builder() {
		return new DomainPropertiesBuilder();
	}

	public static class DomainPropertiesBuilder implements BlueskyPropertiesBuilder<DomainProperties> {

		private List<String> addPathPatternList = new ArrayList<>();

		private List<URI> webList = new ArrayList<>();

		private List<URI> mobileWebList = new ArrayList<>();

		private List<URI> devDomainList = new ArrayList<>();

		private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/",
				"/favicon.ico", "/monitor/", "/support/");

		private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");

		private String requestPath = "/";

		private String forwardPath = "/";

		public DomainPropertiesBuilder addPathPatternList(List<String> addPathPatternList) {
			this.addPathPatternList = addPathPatternList;
			return this;
		}

		public DomainPropertiesBuilder webList(List<URI> webList) {
			this.webList = webList;
			return this;
		}

		public DomainPropertiesBuilder mobileWebList(List<URI> mobileWebList) {
			this.mobileWebList = mobileWebList;
			return this;
		}

		public DomainPropertiesBuilder devDomainList(List<URI> devDomainList) {
			this.devDomainList = devDomainList;
			return this;
		}

		public DomainPropertiesBuilder staticPathList(List<String> staticPathList) {
			this.staticPathList = staticPathList;
			return this;
		}

		public DomainPropertiesBuilder excludePathList(List<String> excludePathList) {
			this.excludePathList = excludePathList;
			return this;
		}

		public DomainPropertiesBuilder requestPath(String requestPath) {
			this.requestPath = requestPath;
			return this;
		}

		public DomainPropertiesBuilder forwardPath(String forwardPath) {
			this.forwardPath = forwardPath;
			return this;
		}

		@Override
		public DomainProperties build() {
			return new DomainProperties(
					addPathPatternList,
					webList,
					mobileWebList,
					devDomainList,
					staticPathList,
					excludePathList,
					requestPath,
					forwardPath);
		}

	}
}
