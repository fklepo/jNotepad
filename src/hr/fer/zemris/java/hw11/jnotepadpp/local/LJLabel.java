package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Objects;

import javax.swing.JLabel;

/**
 * LJLabel represents a localizable {@link JLabel}. If localization change 
 * occurs, this label will change its name depending on the current language.
 * 
 * @author Filip Klepo
 *
 */
public class LJLabel extends JLabel {

	/**
	 * The default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The localization key.
	 */
	private String key;
	/**
	 * The localization provider.
	 */
	private ILocalizationProvider provider;
	
	/**
	 * Constructs instance of this class with given localization arguments.
	 * 
	 * @param key localization key
	 * @param provider localization provider
	 */
	public LJLabel(String key, ILocalizationProvider provider) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(provider);
		
		this.key = key;
		this.provider = provider;
		
		setText(provider.getString(key));
		provider.addLocalizationListener(new ILocalizationListener() {
			@Override
			public void localizationChanged() {
				updateLabel();
			}
		});
	}
	
	/**
	 * Updates the name of this label due to localization change.
	 */
	private void updateLabel() {
		setText(provider.getString(key));
	}
	
}
