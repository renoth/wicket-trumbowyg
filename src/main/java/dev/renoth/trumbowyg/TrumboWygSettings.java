package dev.renoth.trumbowyg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class TrumboWygSettings implements Serializable {

	@Expose
	private final TrumboWygLanguage lang;
	@Expose
	private final HashMap<TrumboWygPlugin, Object> plugins = new HashMap<>();
	@Expose
	private final List<List<TrumboWygButton>> btns = new ArrayList<>();
	@Expose
	private boolean defaultButtons = true;

	private final Set<TrumboWygPlugin> pluginsSet = new HashSet<>();

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

	public TrumboWygSettings withPluginSetting(TrumboWygPlugin name, Object value) {
		plugins.put(name, value);

		return this;
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
