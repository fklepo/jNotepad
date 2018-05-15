package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AbstractLocalizationProvider is a class which partially implements 
 * {@link ILocalizationProvider}. What AbstractLocalizationProvider offers is 
 * storage and notifying of given {@link ILocalizationListener} objects.
 * 
 * @author Filip Klepo
 *
 */
public abstract class AbstractLocalizationProvider 
implements ILocalizationProvider {

	/**
	 * List of listeners.
	 */
	private final List<ILocalizationListener> listeners;
	
	/**
	 * The default constructor.
	 */
	public AbstractLocalizationProvider() {
		listeners = new ArrayList<>();
	}
	
	@Override
	public void addLocalizationListener(ILocalizationListener listener) {
		Objects.requireNonNull(listener);
		
		listeners.add(listener);
	}

	@Override
	public void removeLocalizationListener(ILocalizationListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notifies all registered listeners when change occurs.
	 */
	public void fire() {
		listeners.forEach(t -> t.localizationChanged());
	}

}
