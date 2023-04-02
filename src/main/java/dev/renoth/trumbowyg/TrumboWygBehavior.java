package dev.renoth.trumbowyg;

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

import com.google.gson.Gson;

/**
 * Behavior that adds a Trumbowyg Richtext-Editor to a form component.
 * 
 * @author renoth
 */
public class TrumboWygBehavior extends Behavior {

	private final TrumboWygSettings settings;

	/**
	 * Create a new TrumboWygBehavior
	 * 
	 * @param settings
	 *            The {@link TrumboWygSettings} to be used.
	 */
	public TrumboWygBehavior(TrumboWygSettings settings) {
		this.settings = settings;
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
									"../../../../webjars/trumbowyg/2.27.3/dist/trumbowyg.js")));
			response.render(
					CssHeaderItem.forReference(
							new CssResourceReference(
									TrumboWygBehavior.class,
									"../../../../webjars/trumbowyg/2.27.3/dist/ui/trumbowyg.css")));

			response.render(new OnDomReadyHeaderItem(getInitScript(component)));
		}
	}

	private String getInitScript(Component component) {
		final var handler = new ResourceReferenceRequestHandler(
				new PackageResourceReference(
						TrumboWygBehavior.class,
						"../../../../webjars/trumbowyg/2.27.3/dist/ui/icons.svg"));
		final var svgUrl = RequestCycle.get().urlFor(handler).toString();

		// TODO Provide Settings Factory
		var settingsJson = new Gson().toJson(settings);
		System.out.println(settingsJson);
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
