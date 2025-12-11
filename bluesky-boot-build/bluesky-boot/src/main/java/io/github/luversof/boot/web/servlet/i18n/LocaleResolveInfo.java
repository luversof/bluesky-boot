package io.github.luversof.boot.web.servlet.i18n;

import java.util.Locale;
import java.util.Objects;

/**
 * An object containing information related to locale resolve processing.
 */
public class LocaleResolveInfo {

	private String handlerBeanName;

	private Locale requestLocale;

	private Locale resolveLocale;

	public String getHandlerBeanName() {
		return handlerBeanName;
	}

	public void setHandlerBeanName(String handlerBeanName) {
		this.handlerBeanName = handlerBeanName;
	}

	public Locale getRequestLocale() {
		return requestLocale;
	}

	public void setRequestLocale(Locale requestLocale) {
		this.requestLocale = requestLocale;
	}

	public Locale getResolveLocale() {
		return resolveLocale;
	}

	public void setResolveLocale(Locale resolveLocale) {
		this.resolveLocale = resolveLocale;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LocaleResolveInfo that = (LocaleResolveInfo) o;
		return Objects.equals(handlerBeanName, that.handlerBeanName)
				&& Objects.equals(requestLocale, that.requestLocale)
				&& Objects.equals(resolveLocale, that.resolveLocale);
	}

	@Override
	public int hashCode() {
		return Objects.hash(handlerBeanName, requestLocale, resolveLocale);
	}

	@Override
	public String toString() {
		return "LocaleResolveInfo{" +
				"handlerBeanName='" + handlerBeanName + '\'' +
				", requestLocale=" + requestLocale +
				", resolveLocale=" + resolveLocale +
				'}';
	}
}
