package dev.renoth.trumbowyg;

@SuppressWarnings("java:S115")
public enum TrumboWygPlugin {
	allowtagsfrompaste(false),
	base64(false),
	cleanpaste(false),
	colors(true),
	emoji(true),
	fontfamily(false),
	fontsize(false),
	giphy(true),
	highlight(true),
	history(false),
	indent(false),
	insertaudio(false),
	lineheight(false),
	mathml(true),
	mention(false),
	noembed(false),
	pasteembed(false),
	pasteimage(false),
	preformatted(false),
	resizimg(false),
	ruby(false),
	specialchars(true),
	table(true),
	template(false),
	upload(false);

	private final boolean hasCss;

	TrumboWygPlugin(boolean hasCss) {
		this.hasCss = hasCss;
	}

	public boolean hasCss() {
		return hasCss;
	}
}
