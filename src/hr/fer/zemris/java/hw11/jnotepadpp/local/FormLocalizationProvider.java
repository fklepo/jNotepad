package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * FormLocalizationProvider is a class which acts as a simple connector between
 * given localization provider and a frame. The class acts as a Decorator object
 * around given localization provider.
 * 
 * @author Filip Klepo
 *
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {

	/**
	 * Forms a localization connection of given frame and given provider.
	 * 
	 * @param provider localization provider
	 * @param frame frame to be localized
	 */
	public FormLocalizationProvider(ILocalizationProvider provider, JFrame frame) {
		super(provider);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				connect();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				disconnect();
			}
		});
	}

	
	
}
