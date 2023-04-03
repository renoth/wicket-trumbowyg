package dev.renoth.trumbowyg;

import java.util.Optional;

public enum TrumboWygButton {
	viewHTML,
	undo, redo,
	formatting,
	strong, em, del,
	superscript, subscript,
	link,
	insertImage,
	justifyLeft, justifyCenter, justifyRight, justifyFull,
	unorderedList, orderedList,
	horizontalRule,
	removeformat,
	fullscreen,
	base64(TrumboWygPlugin.base64),
	foreColor(TrumboWygPlugin.colors), backColor(TrumboWygPlugin.colors),
	emoji(TrumboWygPlugin.emoji),
	fontfamily(TrumboWygPlugin.fontfamily),
	fontsize(TrumboWygPlugin.fontsize),
	giphy(TrumboWygPlugin.giphy),
	historyUndo(TrumboWygPlugin.history), historyRedo(TrumboWygPlugin.history),
	indent(TrumboWygPlugin.indent), outdent(TrumboWygPlugin.indent),
	insertAudio(TrumboWygPlugin.insertaudio),
	lineheight(TrumboWygPlugin.lineheight),
	mathml(TrumboWygPlugin.mathml),
	mention(TrumboWygPlugin.mention),
	noembed(TrumboWygPlugin.noembed),
	preformatted(TrumboWygPlugin.preformatted),
	ruby(TrumboWygPlugin.ruby),
	specialChars(TrumboWygPlugin.specialchars),
	table(TrumboWygPlugin.table),
	tableAddHeaderRow(TrumboWygPlugin.table),
	tableAddRowAbove(TrumboWygPlugin.table),
	tableAddRow(TrumboWygPlugin.table),
	tableDeleteRow(TrumboWygPlugin.table),
	tableAddColumnLeft(TrumboWygPlugin.table),
	tableAddColumn(TrumboWygPlugin.table),
	tableDeleteColumn(TrumboWygPlugin.table),
	tableVerticalAlignTop(TrumboWygPlugin.table),
	tableVerticalAlignMiddle(TrumboWygPlugin.table),
	tableVerticalAlignBottom(TrumboWygPlugin.table),
	tableMergeCells(TrumboWygPlugin.table),
	tableUnmergeCells(TrumboWygPlugin.table),
	tableDestroy(TrumboWygPlugin.table),
	tableCellBackgroundColor(TrumboWygPlugin.table),
	tableBorderColor(TrumboWygPlugin.table),
	template(TrumboWygPlugin.template),
	upload(TrumboWygPlugin.upload);

	private TrumboWygPlugin requiredPlugin = null;

	TrumboWygButton() {
	}

	TrumboWygButton(TrumboWygPlugin requriedPlugin) {
		this.requiredPlugin = requriedPlugin;
	}

	public Optional<TrumboWygPlugin> getRequiredPlugin() {
		return Optional.ofNullable(requiredPlugin);
	}

}
