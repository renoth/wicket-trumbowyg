package dev.renoth.trumbowyg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TrumboWygSettings implements Serializable {
	private final TrumboWygLanguage lang;
	private final Set<TrumboWygPlugin> pluginsSet = new HashSet<>();
	private final List<List<TrumboWygButton>> btns = new ArrayList<>();
	private boolean defaultButtons = true;

	private TrumboWygSettings(TrumboWygLanguage lang) {
		this.lang = lang;
	}

	public static TrumboWygSettings getInstance(TrumboWygLanguage lang) {
		return new TrumboWygSettings(lang);
	}

	public Set<TrumboWygPlugin> getPlugins() {
		return pluginsSet;
	}

	public TrumboWygLanguage getLang() {
		return lang;
	}

	public boolean isDefaultButtons() {
		return defaultButtons;
	}

	public TrumboWygButtonBuilder addButtons() {
		this.defaultButtons = false;

		return new TrumboWygButtonBuilder(this);
	}

	public List<List<TrumboWygButton>> getBtns() {
		return btns;
	}

	public static class TrumboWygButtonBuilder {
		private final TrumboWygSettings settings;

		public TrumboWygButtonBuilder(TrumboWygSettings settings) {
			this.settings = settings;
		}

		public TrumboWygButtonBuilder withButtonGroup(TrumboWygButton... buttons) {
			var buttonList = Arrays.asList(buttons);
			settings.getBtns().add(buttonList);
			settings.getPlugins().addAll(
					buttonList.stream()
							.filter(button -> button.getRequiredPlugin().isPresent())
							.map(button -> button.getRequiredPlugin().get())
							.collect(Collectors.toSet()));

			return this;
		}

		public TrumboWygSettings done() {
			return settings;
		}
	}
}
