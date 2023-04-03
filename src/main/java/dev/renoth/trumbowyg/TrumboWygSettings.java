package dev.renoth.trumbowyg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrumboWygSettings implements Serializable {
	private String prefix;
	private final TrumboWygLanguage lang;
	private final Set<TrumboWygPlugin> pluginsSet = new HashSet<>();
	private final List<List<TrumboWygButton>> btns = new ArrayList<>();

	private TrumboWygSettings(TrumboWygLanguage lang) {
		this.lang = lang;
	}

	public static TrumboWygSettings getInstance(TrumboWygLanguage lang) {
		return new TrumboWygSettings(lang);
	}

	public TrumboWygSettings withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public TrumboWygSettings withPlugins(TrumboWygPlugin... plugins) {
		Collections.addAll(pluginsSet, plugins);

		return this;
	}

	public Set<TrumboWygPlugin> getPlugins() {
		return pluginsSet;
	}

	public TrumboWygLanguage getLang() {
		return lang;
	}

	public TrumboWygButtonBuilder addButtons() {
		return new TrumboWygButtonBuilder(this);
	}

	private List<List<TrumboWygButton>> getBtns() {
		return btns;
	}

	public static class TrumboWygButtonBuilder {
		private final TrumboWygSettings settings;

		public TrumboWygButtonBuilder(TrumboWygSettings settings) {
			this.settings = settings;
		}

		public TrumboWygButtonBuilder withButtonGroup(TrumboWygButton... buttons) {
			settings.getBtns().add(Arrays.asList(buttons));

			return this;
		}

		public TrumboWygSettings done() {
			return settings;
		}
	}
}
