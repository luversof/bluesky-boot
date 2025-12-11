package io.github.luversof.boot.web;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;

@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebProperties extends AbstractBlueskyProperties<WebProperties, WebProperties.WebPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	public static final String PREFIX = "bluesky-boot.web";

	/**
	 * Whether to check unsupported browsers
	 */
	private Boolean checkNotSupportedBrowser = true;

	/**
	 * Unsupported browser check pattern list
	 */
	private List<String> notSupportedBrowserRegPatternList = List.of(".*(MSIE [5-9]).*");

	/**
	 * Registering an exception list address pattern when checking for unsupported
	 * browser
	 */
	private List<String> notSupportedBrowserExcludePathPatternList = List.of("/css/**", "/html/**", "/js/**", "/img/**",
			"/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**");

	public WebProperties() {
	}

	public WebProperties(Boolean checkNotSupportedBrowser, List<String> notSupportedBrowserRegPatternList,
			List<String> notSupportedBrowserExcludePathPatternList) {
		this.checkNotSupportedBrowser = checkNotSupportedBrowser;
		this.notSupportedBrowserRegPatternList = notSupportedBrowserRegPatternList;
		this.notSupportedBrowserExcludePathPatternList = notSupportedBrowserExcludePathPatternList;
	}

	public Boolean getCheckNotSupportedBrowser() {
		return checkNotSupportedBrowser;
	}

	public void setCheckNotSupportedBrowser(Boolean checkNotSupportedBrowser) {
		this.checkNotSupportedBrowser = checkNotSupportedBrowser;
	}

	public List<String> getNotSupportedBrowserRegPatternList() {
		return notSupportedBrowserRegPatternList;
	}

	public void setNotSupportedBrowserRegPatternList(List<String> notSupportedBrowserRegPatternList) {
		this.notSupportedBrowserRegPatternList = notSupportedBrowserRegPatternList;
	}

	public List<String> getNotSupportedBrowserExcludePathPatternList() {
		return notSupportedBrowserExcludePathPatternList;
	}

	public void setNotSupportedBrowserExcludePathPatternList(List<String> notSupportedBrowserExcludePathPatternList) {
		this.notSupportedBrowserExcludePathPatternList = notSupportedBrowserExcludePathPatternList;
	}

	@Override
	protected BiConsumer<WebProperties, WebPropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			if (properties == null) {
				return;
			}
			var propertyMapper = PropertyMapper.get();
			propertyMapper.from(properties::getCheckNotSupportedBrowser).to(builder::checkNotSupportedBrowser);
			propertyMapper.from(properties::getNotSupportedBrowserRegPatternList)
					.to(builder::notSupportedBrowserRegPatternList);
			propertyMapper.from(properties::getNotSupportedBrowserExcludePathPatternList)
					.to(builder::notSupportedBrowserExcludePathPatternList);
		};
	}

	@Override
	protected WebPropertiesBuilder getBuilder() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		return parentModuleInfo == null ? WebProperties.builder() : parentModuleInfo.getWebPropertiesBuilder();
	}

	public static WebPropertiesBuilder builder() {
		return new WebPropertiesBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		WebProperties that = (WebProperties) o;
		return Objects.equals(checkNotSupportedBrowser, that.checkNotSupportedBrowser) &&
				Objects.equals(notSupportedBrowserRegPatternList, that.notSupportedBrowserRegPatternList) &&
				Objects.equals(notSupportedBrowserExcludePathPatternList,
						that.notSupportedBrowserExcludePathPatternList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), checkNotSupportedBrowser, notSupportedBrowserRegPatternList,
				notSupportedBrowserExcludePathPatternList);
	}

	@Override
	public String toString() {
		return "WebProperties{" +
				"checkNotSupportedBrowser=" + checkNotSupportedBrowser +
				", notSupportedBrowserRegPatternList=" + notSupportedBrowserRegPatternList +
				", notSupportedBrowserExcludePathPatternList=" + notSupportedBrowserExcludePathPatternList +
				'}';
	}

	public static class WebPropertiesBuilder implements BlueskyPropertiesBuilder<WebProperties> {

		private Boolean checkNotSupportedBrowser = true;

		private List<String> notSupportedBrowserRegPatternList = List.of(".*(MSIE [5-9]).*");

		private List<String> notSupportedBrowserExcludePathPatternList = List.of("/css/**", "/html/**", "/js/**",
				"/img/**", "/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**");

		public WebPropertiesBuilder checkNotSupportedBrowser(Boolean checkNotSupportedBrowser) {
			this.checkNotSupportedBrowser = checkNotSupportedBrowser;
			return this;
		}

		public WebPropertiesBuilder notSupportedBrowserRegPatternList(List<String> notSupportedBrowserRegPatternList) {
			this.notSupportedBrowserRegPatternList = notSupportedBrowserRegPatternList;
			return this;
		}

		public WebPropertiesBuilder notSupportedBrowserExcludePathPatternList(
				List<String> notSupportedBrowserExcludePathPatternList) {
			this.notSupportedBrowserExcludePathPatternList = notSupportedBrowserExcludePathPatternList;
			return this;
		}

		@Override
		public WebProperties build() {
			return new WebProperties(
					checkNotSupportedBrowser,
					notSupportedBrowserRegPatternList,
					notSupportedBrowserExcludePathPatternList);
		}
	}
}
