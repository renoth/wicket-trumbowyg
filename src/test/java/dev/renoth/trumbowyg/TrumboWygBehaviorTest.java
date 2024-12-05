package dev.renoth.trumbowyg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.cycle.RequestCycle;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TrumboWygBehaviorTest {

	@Test
	void defaultSettings() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de);
		var cut = new TrumboWygBehavior(settings);

		// Act
		String result = getInitScript(cut);

		// Assert
		assertThat(result).doesNotContain("btns", "pluginsSet", "defaultButtons");
		assertThat(result).contains(".trumbowyg({\"lang\":\"de\"});");
		assertThat(result).contains("$('#id123').trumbowyg(");
	}

	@Test
	void customSettings() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de)
				.withCustomSetting(TrumboWygCustomSettings.autogrow, "true")
				.withCustomListSetting(TrumboWygCustomListSettings.tagsToRemove, List.of("script", "strong"))
				.withCustomMapSetting(TrumboWygCustomMapSettings.semantic, Map.of("b", "strong", "strike", "del"))
				.withCustomMapSetting(TrumboWygCustomMapSettings.tagClasses, Map.of("ul", "standard"));
		var cut = new TrumboWygBehavior(settings);

		// Act
		String result = getInitScript(cut);

		// Assert
		assertThat(result).doesNotContain("btns", "pluginsSet", "defaultButtons");
		assertThat(result).contains("\"autogrow\":\"true\"");
		assertThat(result).contains("\"tagsToRemove\":[\"script\",\"strong\"]");
		assertThat(result).contains("\"tagClasses\":{\"ul\":\"standard\"}");
		assertThat(result).contains("\"semantic\":{");
	}

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
		var cut = new TrumboWygBehavior(settings);

		// Act
		String result = getInitScript(cut);

		// Assert
		assertThat(settings.getPlugins())
				.containsExactlyInAnyOrder(
						TrumboWygPlugin.noembed,
						TrumboWygPlugin.fontfamily,
						TrumboWygPlugin.fontsize);
		assertThat(result).contains("\"btns\":[[\"noembed\",\"del\"]");
		assertThat(result).contains("\"lang\":\"de\"");
	}

	@Test
	void testWithEventCallbacks() {
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
										Map.of("name", "Times New Roman", "family", "times new roman"))))
				.withCustomEventCallback(TrumboWygEvent.tbwchange, "console.log('Changed');")
				.withCustomEventCallback(TrumboWygEvent.tbwfocus, "console.log('Focused');");
		var cut = new TrumboWygBehavior(settings);

		// Act
		String result = getInitScript(cut);

		// Assert
		assertThat(settings.getPlugins())
				.containsExactlyInAnyOrder(
						TrumboWygPlugin.noembed,
						TrumboWygPlugin.fontfamily,
						TrumboWygPlugin.fontsize);
		assertThat(result).contains("\"btns\":[[\"noembed\",\"del\"]");
		assertThat(result).contains("\"lang\":\"de\"");
		assertThat(result).contains(".on('tbwchange', function(){console.log('Changed');})");
		assertThat(result).contains(".on('tbwfocus', function(){console.log('Focused');})");
	}

	@Test
	void testWithUpdateOnChange() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de)
				.withUpdateOnChange(true)
				.withOnChangeThrottleMs(250);
		var cut = new TrumboWygBehavior(settings);

		// Act
		String result = getInitScript(cut);

		// Assert
		assertThat(result).contains("document.getElementById('id123').value = $('#id123').trumbowyg('html');");
	}

	@Test
	void setLanguage() {
		// Arrange
		var settings = TrumboWygSettings.getInstance(TrumboWygLanguage.de);
		var cut = new TrumboWygBehavior(settings);

		// Act
		String result = getInitScript(cut);

		// Assert
		assertThat(result).doesNotContain("btns", "pluginsSet", "defaultButtons");
	}

	private static String getInitScript(TrumboWygBehavior cut) {
		String result;

		try (MockedStatic<RequestCycle> requestCycleMockedStatic = mockStatic(RequestCycle.class)) {
			var requestCycleMock = mock(RequestCycle.class);
			when(requestCycleMock.urlFor(any())).thenReturn("http://test");
			requestCycleMockedStatic.when(RequestCycle::get).thenReturn(requestCycleMock);
			var component = mock(Label.class);
			when(component.getMarkupId()).thenReturn("id123");
			result = cut.getInitScript(component);
		}

		return result;
	}

}