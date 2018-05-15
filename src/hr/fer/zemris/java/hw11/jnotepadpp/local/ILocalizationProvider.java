package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * ILocalizationProvider is an interface which represents localization 
 * providers. Each localization provider must support registration of 
 * interested objects and localization of given strings by localization keys.
 * 
 * @author Filip Klepo
 *
 */
public interface ILocalizationProvider {

	/**
	 * Adds a localization listener.
	 * 
	 * @param listener new listener
	 */
	public void addLocalizationListener(ILocalizationListener listener);
	
	/**
	 * Removes localization listener.
	 * 
	 * @param listener listener to be removed
	 */
	public void removeLocalizationListener(ILocalizationListener listener);
	
	/**
	 * Gets localized string from given key.
	 * 
	 * @param key localization key
	 * @return localized string
	 */
	public String getString(String key);
	
}
