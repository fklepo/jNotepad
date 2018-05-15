package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Objects;

/**
 * LocalizationProviderBridge is a class which acts as a bridge between 
 * localization provider and a frame. This class enables frames to 
 * 
 * @author Filip Klepo
 *
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {

	/**
	 * The localization provider.
	 */
	private ILocalizationProvider provider;
	/**
	 * Flag which indicates wheter the bridge is connected or not.
	 */
	private boolean connected;
	/**
	 * Listener which is registered to given localization provider.
	 */
	private ILocalizationListener listener;
	
	/**
	 * Constructs instance of this class with given localization provider.
	 * 
	 * @param provider localization provider
	 */
	public LocalizationProviderBridge(ILocalizationProvider provider) {
		Objects.requireNonNull(provider);
		this.provider = provider;
	}
	
	/**
	 * Connects to a localization provider.
	 */
	public void connect() {
		if(!connected) {
			connected = true;
			listener = new ILocalizationListener() {
				@Override
				public void localizationChanged() {
					LocalizationProviderBridge.this.fire();
				}
			};
			provider.addLocalizationListener(listener);
		}
	}
	
	/**
	 * Disconnects from localization provider.
	 */
	public void disconnect() {
		provider.removeLocalizationListener(listener);
	}
	
	
	@Override
	public String getString(String key) {
		return provider.getString(key);
	}

}
