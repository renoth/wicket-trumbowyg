package dev.renoth.trumbowyg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TrumboWygBehaviorTest {

	@Test
	void test() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de).addButtons()
				.withButtonGroup(TrumboWygButton.noembed, TrumboWygButton.del).done();

		// Act

		// Assert
		Assertions.assertTrue(settings.getPlugins().contains(TrumboWygPlugin.noembed));
	}

	@Test
	void setLanguage() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de).addButtons().done();

		// Act

		// Assert
	}

}