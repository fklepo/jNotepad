package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * ILocalizationListener is a functional interface. 
 * It represents a localization listener.
 * 
 * @author Filip Klepo
 *
 */
@FunctionalInterface
public interface ILocalizationListener {

	/**
	 * Method which is called by {@link ILocalizationProvider} objects each
	 * time localization change has occurred.
	 */
	public void localizationChanged();
	
}
