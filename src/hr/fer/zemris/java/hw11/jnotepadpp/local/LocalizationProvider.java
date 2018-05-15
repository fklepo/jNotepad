package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * LocalizationProvider is a fully-functional {@link ILocalizationProvider}.
 * It acts as a singleton object, meaning that only one instance of this class
 * will exist during program runtime. LocalizationProvider enables 
 * internationalization of given strings and setting the language.
 * 
 * @author Filip Klepo
 *
 */
public class LocalizationProvider extends AbstractLocalizationProvider {

	/**
	 * Current localization language.
	 */
	private String language;
	/**
	 * Current localization resource bundle.
	 */
	private ResourceBundle bundle;
	/**
	 * Starting language for this localization provider.
	 */
	private final static String STARTING_LANGUAGE = LocalizationLanguages.EN;
	/**
	 * Path to directory which holds localization .properties files.
	 */
	private final static String BASE_NAME = 
			"hr.fer.zemris.java.hw11.jnotepadpp.local.translations";
	/**
	 * Instance of this class.
	 */
	private final static LocalizationProvider INSTANCE = 
			new LocalizationProvider();
	
	/**
	 * The current resource bundle locale.
	 */
	private Locale curLocale;
	/**
	 * The default constructor. 
	 */
	private LocalizationProvider() {
		language = STARTING_LANGUAGE;
		bundle = ResourceBundle.getBundle(BASE_NAME, 
				Locale.forLanguageTag(language));
	}
	
	/**
	 * Gets instance of this class.
	 * @return instance of this class
	 */
	public static LocalizationProvider getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Sets new language for this localization provider.
	 * 
	 * @param language new language
	 */
	public void setLanguage(String language) {
		Objects.requireNonNull(language);
		//retrieval of ResourceBundle is an expensive operation
		if(this.language.equals(language)) {
			return;
		}
		
		this.language = language;
		this.curLocale = Locale.forLanguageTag(language);
		bundle = ResourceBundle.getBundle(BASE_NAME, curLocale);
		fire();
	}
	
	/**
	 * Gets current language's representation of word defined by given key.
	 * 
	 * @param key key which represents a string obtained as value of currently
	 * set language
	 * @return translated word
	 */
	public String getString(String key) {
		String res = bundle.getString(key);
		try {
			return new String(res.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return res;
		}
	}
	
	/**
	 * Gets current locale.
	 * 
	 * @return current locale
	 */
	public Locale getLocale() {
		return curLocale == null ? new Locale(STARTING_LANGUAGE) 
								 : (Locale) curLocale.clone();
	}
	
}
