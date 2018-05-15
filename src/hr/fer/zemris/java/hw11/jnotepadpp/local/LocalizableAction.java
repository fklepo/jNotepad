package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * LocalizableAction is {@link AbstractAction} which enables dynamical change
 * of its properties, dictated by localization listener.
 * 
 * @author Filip Klepo
 *
 */
public abstract  class LocalizableAction extends AbstractAction {
	
	/**
	 * The default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The localization key for action name.
	 */
	private String nameKey;
	/**
	 * The localization key for action description.
	 */
	private String shortDescKey;
	/**
	 * Flag which indicates whether this action has description or not.
	 */
	private boolean isShortlyDescribable;
	
	/**
	 * Constructs instance of this class with given arguments.
	 * 
	 * @param nameKey localization key for action name
	 * @param shortDescKey localization key for short description of action
	 * @param provider localization provider
	 */
	public LocalizableAction(String nameKey, String shortDescKey, 
			ILocalizationProvider provider) {
		Objects.requireNonNull(nameKey);
		Objects.requireNonNull(provider);
		
		this.nameKey = nameKey;
		this.shortDescKey = shortDescKey;
		this.putValue(Action.NAME, provider.getString(this.nameKey));
		if(shortDescKey != null) {
			isShortlyDescribable = true;
			this.putValue(Action.SHORT_DESCRIPTION, 
					provider.getString(shortDescKey));
		}
		
		provider.addLocalizationListener(new ILocalizationListener() {
			@Override
			public void localizationChanged() {
				LocalizableAction.this.putValue(Action.NAME, 
						provider.getString(LocalizableAction.this.nameKey));
				if(isShortlyDescribable) {
					LocalizableAction.this.putValue(Action.SHORT_DESCRIPTION,
							provider.getString(
									LocalizableAction.this.shortDescKey));
				}
			}
		});
	}

}
