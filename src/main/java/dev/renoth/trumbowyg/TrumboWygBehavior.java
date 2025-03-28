package dev.renoth.trumbowyg;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Behavior that adds a Trumbowyg Richtext-Editor to a form component.
 * 
 * @author Johannes Renoth
 */
public class TrumboWygBehavior extends Behavior {

	private static final Logger LOG = LoggerFactory.getLogger(TrumboWygBehavior.class);
	private static final String TRUMBOWYG_RESOURCE_PATH = "../../../webjars/trumbowyg/2.28.0/dist";

	private final TrumboWygSettings settings;

	/**
	 * Create a new TrumboWygBehavior
	 * 
	 * @param settings
	 *            The {@link TrumboWygSettings} to be used.
	 */
	public TrumboWygBehavior(TrumboWygSettings settings) {
		this.settings = settings;
		checkSettings();
	}

	private void checkSettings() {
		settings.getBtns().stream().flatMap(Collection::stream)
				.filter(
						btn -> btn.getRequiredPlugin().isPresent()
								&& !settings.getPlugins().contains(btn.getRequiredPlugin().get()))
				.forEachOrdered(
						btn -> LOG.warn(
								"{} requires Plugin {} but is not loaded",
								btn.name(),
								btn.getRequiredPlugin().orElseGet(null)));
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);

		if (component == null) {
			throw new IllegalStateException("TrumboWygBehavior is not bound to a component");
		}
		else {
			response.render(
					JavaScriptHeaderItem.forReference(
							new TrumbowygJavaScriptResourceReference(
									TrumboWygBehavior.class,
									TRUMBOWYG_RESOURCE_PATH + "/trumbowyg.js")));
			response.render(
					CssHeaderItem.forReference(
							new CssResourceReference(
									TrumboWygBehavior.class,
									TRUMBOWYG_RESOURCE_PATH + "/ui/trumbowyg.css")));

			response.render(
					JavaScriptHeaderItem.forReference(
							new JavaScriptResourceReference(
									TrumboWygBehavior.class,
									String.format(
											"%1$s/langs/%2$s.js",
											TRUMBOWYG_RESOURCE_PATH,
											settings.getLang().name()))));

			settings.getPlugins().forEach(
					p -> {
						response.render(
								JavaScriptHeaderItem.forReference(
										new JavaScriptResourceReference(
												TrumboWygBehavior.class,
												String.format(
														"%1$s/plugins/%2$s/trumbowyg.%2$s.js",
														TRUMBOWYG_RESOURCE_PATH,
														p.name()))));

						if (p.hasCss()) {
							response.render(
									CssHeaderItem.forReference(
											new CssResourceReference(
													TrumboWygBehavior.class,
													String.format(
															"%1$s/plugins/%2$s/ui/trumbowyg.%2$s.css",
															TRUMBOWYG_RESOURCE_PATH,
															p.name()))));
						}
					});

			response.render(new OnDomReadyHeaderItem(getInitScript(component)));
		}
	}

	String getInitScript(Component component) {
		final var handler = new ResourceReferenceRequestHandler(
				new PackageResourceReference(
						TrumboWygBehavior.class,
						TRUMBOWYG_RESOURCE_PATH + "/ui/icons.svg"));
		final var svgUrl = RequestCycle.get().urlFor(handler).toString();
		final var markupId = component.getMarkupId();

		var settingsJson = new GsonBuilder()
				.registerTypeAdapter(TrumboWygSettings.class, new TrumboWygSettingsJsonSerializer()).create()
				.toJson(settings);
		LOG.debug("Settings: {}", settingsJson);

		var script = new StringBuilder(
				String.format(
						"$.trumbowyg.svgPath = '%1$s';$('#%2$s').trumbowyg(%3$s)",
						svgUrl,
						markupId,
						settingsJson));

		settings.getCustomEventCallbacks()
				.forEach((key, value) -> script.append(".on('%s', function(){%s})".formatted(key, value)));

		if (settings.isUpdateOnChange()) {
			script.append(
					(".on('%1$s', function(){ if (window.trumbowyg_timeout_ref != null) {clearTimeout(window.trumbowyg_timeout_ref);} "
							+ "window.trumbowyg_timeout_ref = setTimeout(() => {document.getElementById('%2$s').value = $('#%2$s').trumbowyg('html'); "
							+ "document.getElementById('%2$s').dispatchEvent(new Event('change'));}, %3$s); })")
							.formatted(TrumboWygEvent.tbwchange, markupId, settings.getOnChangeThrottleMs()));
		}

		script.append(";");

		return script.toString();
	}

	@Override
	public void bind(Component component) {
		super.bind(component);

		component.setOutputMarkupId(true);
	}

	private static class TrumbowygJavaScriptResourceReference extends JavaScriptResourceReference {

		public TrumbowygJavaScriptResourceReference(Class<?> scope, String name) {
			super(scope, name);
		}

		@Override
		public List<HeaderItem> getDependencies() {
			ResourceReference backingLibraryReference;

			if (Application.exists()) {
				backingLibraryReference = Application.get().getJavaScriptLibrarySettings().getJQueryReference();
			}
			else {
				backingLibraryReference = JQueryResourceReference.getV3();
			}

			final var dependencies = super.getDependencies();
			dependencies.add(JavaScriptHeaderItem.forReference(backingLibraryReference));

			return dependencies;
		}
	}

	private static class TrumboWygSettingsJsonSerializer implements JsonSerializer<TrumboWygSettings> {
		@Override
		public JsonElement serialize(TrumboWygSettings src, Type typeOfSrc, JsonSerializationContext context) {
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			JsonObject jObj = (JsonObject) gson.toJsonTree(src);

			if (src.isDefaultButtons()) {
				jObj.remove("btns");
				jObj.remove("plugins");
			}

			src.getCustomSettings().forEach((key, value) -> jObj.addProperty(key.name(), value));
			src.getCustomListSettings().forEach((key, value) -> jObj.add(key.name(), gson.toJsonTree(value)));
			src.getCustomMapSettings().forEach((key, value) -> jObj.add(key.name(), gson.toJsonTree(value)));

			return jObj;
		}
	}
}
