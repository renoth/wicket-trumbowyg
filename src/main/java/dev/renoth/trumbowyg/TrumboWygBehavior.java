package dev.renoth.trumbowyg;

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

/**
 * Behavior that adds a Trumbowyg Richtext-Editor to a form component.
 * 
 * @author renoth
 */
public class TrumboWygBehavior extends Behavior {

	private static final Logger LOG = LoggerFactory.getLogger(TrumboWygBehavior.class);
	private static final String TRUMBOWYG_RESOURCE_PATH = "../../../webjars/trumbowyg/2.27.3/dist";

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
								btn.getRequiredPlugin().get()));
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
						response.render(
								CssHeaderItem.forReference(
										new CssResourceReference(
												TrumboWygBehavior.class,
												String.format(
														"%1$s/plugins/%2$s/ui/trumbowyg.%2$s.css",
														TRUMBOWYG_RESOURCE_PATH,
														p.name()))));
					});

			response.render(new OnDomReadyHeaderItem(getInitScript(component)));
		}
	}

	private String getInitScript(Component component) {
		final var handler = new ResourceReferenceRequestHandler(
				new PackageResourceReference(
						TrumboWygBehavior.class,
						TRUMBOWYG_RESOURCE_PATH + "/ui/icons.svg"));
		final var svgUrl = RequestCycle.get().urlFor(handler).toString();

		var settingsJson = new Gson().toJson(settings);
		LOG.debug("Settings: %s".formatted(settingsJson));

		return String.format(
				"$.trumbowyg.svgPath = '%1$s';$('#%2$s').trumbowyg(%3$s);",
				svgUrl,
				component.getMarkupId(),
				settingsJson);
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
}
