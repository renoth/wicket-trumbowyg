package dev.renoth.trumbowyg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;

class TrumboWygBehaviorTest {

	@Test
	void test() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de).addButtons()
				.withButtonGroup(TrumboWygButton.noembed, TrumboWygButton.del)
				.withButtonGroup(TrumboWygButton.fontfamily, TrumboWygButton.fontsize).done()
				.withPluginSetting(
						TrumboWygPlugin.fontsize,
						Map.of(
								TrumboWygPluginSettings.fontsize_sizeList,
								Arrays.asList("10px", "12px", "14px")))
				.withPluginSetting(
						TrumboWygPlugin.fontfamily,
						Map.of(
								TrumboWygPluginSettings.fontfamily_fontList,
								List.of(
										Map.of("name", "Arial", "family", "Arial, Helvetica, sans-serif"),
										Map.of("name", "Times New Roman", "family", "times new roman"))));
		// Act

		// Assert
		Assertions.assertTrue(settings.getPlugins().contains(TrumboWygPlugin.noembed));

		System.out.println(
				new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(settings));
	}

	@Test
	void setLanguage() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de).addButtons().done();

		// Act

		// Assert
	}

}