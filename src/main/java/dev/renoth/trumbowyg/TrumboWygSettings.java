package dev.renoth.trumbowyg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class TrumboWygSettings implements Serializable {

	@Expose
	private final TrumboWygLanguage lang;
	@Expose
	private final Map<TrumboWygPlugin, Object> plugins = new EnumMap<>(TrumboWygPlugin.class);
	@Expose
	private final List<List<TrumboWygButton>> btns = new ArrayList<>();

	private boolean defaultButtons = true;
	private final Set<TrumboWygPlugin> pluginsSet = new HashSet<>();
	private final Map<TrumboWygCustomSettings, String> customSettings = new EnumMap<>(TrumboWygCustomSettings.class);
	private final Map<TrumboWygCustomListSettings, List<String>> customListSettings =
			new EnumMap<>(TrumboWygCustomListSettings.class);
	private final Map<TrumboWygCustomMapSettings, Map<String, String>> customMapSettings =
			new EnumMap<>(TrumboWygCustomMapSettings.class);

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

	public TrumboWygSettings withCustomSetting(TrumboWygCustomSettings name, String value) {
		customSettings.put(name, value);

		return this;
	}

	public TrumboWygSettings withCustomListSetting(TrumboWygCustomListSettings name, List<String> value) {
		customListSettings.put(name, value);

		return this;
	}

	public TrumboWygSettings withCustomMapSetting(TrumboWygCustomMapSettings name, Map<String, String> value) {
		customMapSettings.put(name, value);

		return this;
	}

	public List<List<TrumboWygButton>> getBtns() {
		return btns;
	}

	public Map<TrumboWygCustomSettings, String> getCustomSettings() {
		return customSettings;
	}

	public Map<TrumboWygCustomListSettings, List<String>> getCustomListSettings() {
		return customListSettings;
	}

	public Map<TrumboWygCustomMapSettings, Map<String, String>> getCustomMapSettings() {
		return customMapSettings;
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
