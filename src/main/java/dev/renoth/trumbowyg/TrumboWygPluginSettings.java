package dev.renoth.trumbowyg;

public enum TrumboWygPluginSettings {
	/**
	 * Expected is a list of Fonzsizes (e.g. Arrays.asList("10px", "12px", "14px"))
	 */
	fontsize_sizeList("sizeList"),
	/**
	 * Expected is a List of Map
	 */
	fontfamily_fontList("fontList");

	private final String settingName;

	TrumboWygPluginSettings(String setting) {
		this.settingName = setting;
	}

	@Override
	public String toString() {
		return settingName;
	}
}
