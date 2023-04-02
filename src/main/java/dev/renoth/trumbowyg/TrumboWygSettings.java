package dev.renoth.trumbowyg;

public class TrumboWygSettings {
	private String prefix;
	private String lang;

	public static TrumboWygSettings getInstance() {
		return new TrumboWygSettings();
	}

	public TrumboWygSettings withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public TrumboWygSettings withLang(String lang) {
		this.lang = lang;
		return this;
	}
}
