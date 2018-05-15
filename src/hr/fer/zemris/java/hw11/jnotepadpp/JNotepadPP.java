package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import hr.fer.zemris.java.hw11.jnotepadpp.local.FormLocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationListener;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizableAction;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationKeys;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationLanguages;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProvider;

/**
 * JNotepadPP is a class which models a simple Notepad++-like textual editor. 
 * Its interface towards users is fairly simple, one can: <ul>
 * <li>Create a new document</li> <li>Open a new document</li> 
 * <li>Save edited document</li> <li>Cut/Copy/Paste selected text</li> 
 * <li>Use variety of textual tools, such as text sorting</li> </ul> and so on..
 * <p>The editor can receive commands by both keyboard and mouse. One can create
 * a new window by clicking ctrl-N on his keyboard, or close the currently 
 * opened document by clicking the middle button (the scroll button) of his 
 * mouse.</p>
 * 
 * @author Filip Klepo
 *
 */
public class JNotepadPP extends JFrame {

	/**
	 * The default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The frame name.
	 */
	private static final String FRAME_NAME = "JNotepad ++";
	/**
	 * Path to edited document icon.
	 */
	private static final String EDITED_ICON_PATH = "icons/floppy_red.png";
	/**
	 * Path to unedited document icon.
	 */
	private static final String UNEDITED_ICON_PATH = "icons/floppy_green.png";
	/**
	 * Path to frame icon.
	 */
	private static final String NPP_ICON_PATH = "icons/n++icon.png";
	/**
	 * Unedited document icon.
	 */
	private static ImageIcon UNEDITED_ICON;
	/**
	 * Edited document icon.
	 */
	private static ImageIcon EDITED_ICON;
	
	/**
	 * The tabbed pane.
	 */
	private JTabbedPane tabbedPane;
	/**
	 * Currently opened file paths. If any of current documents is not yet 
	 * saved, null is put in its position.
	 */
	private List<Path> openedFilePaths;
	/**
	 * The toolbar.
	 */
	private JToolBar toolBar;
	
	/**
	 * Date-time formatter.
	 */
	private SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	/**
	 * The status panel.
	 */
	private JToolBar statusPanel;
	/**
	 * The time label.
	 */
	private JLabel jlTime;
	/**
	 * The length label.
	 */
	private JLabel sbLength;
	/**
	 * The status bar line-column-selected label.
	 */
	private JLabel sbLnColSel;
	/**
	 * The timer.
	 */
	private Timer timer;
	/**
	 * Length of currently opened document.
	 */
	private String lengthText;
	/**
	 * Line in which caret is currently positioned.
	 */
	private String lnText;
	/**
	 * Column in which caret is.
	 */
	private String colText;
	/**
	 * Length of selected text.
	 */
	private String selText;
	/**
	 * Spacing between status bar components.
	 */
	private final static String SPACING = "     ";
	
	/**
	 * The engine behind the process of localization of this program. Enables
	 * simple translation of given strings.
	 */
	private FormLocalizationProvider flp = new FormLocalizationProvider(
			LocalizationProvider.getInstance(), this);
	
	/**
	 * To upper case item.
	 */
	private JMenuItem toUpperCaseItem;
	/**
	 * To lower case item.
	 */
	private JMenuItem toLowerCaseItem;
	/**
	 * The invert case item.
	 */
	private JMenuItem invertCaseItem;
	/**
	 * The sort ascending item.
	 */
	private JMenuItem sortAscendingItem;
	/**
	 * The sort descending item.
	 */
	private JMenuItem sortDescendingItem;
	/**
	 * The unique lines item.
	 */
	private JMenuItem uniqueLinesItem;
	/**
	 * Empty name toolbar buttons.
	 */
	private List<JButton> emptyNameToolbarButtons;
	
	/**
	 * The default constructor. Initializes the {@link JNotepadPP} frame.
	 */
	public JNotepadPP() {
		UNEDITED_ICON = getImageIcon(UNEDITED_ICON_PATH);
		EDITED_ICON = getImageIcon(EDITED_ICON_PATH);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLocation(100, 100);
		setSize(600, 600);
		setTitle(FRAME_NAME);
		setIconImage(getImageIcon(NPP_ICON_PATH).getImage());
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		initGui();
	}
	
	/**
	 * Initializes the GUI.
	 */
	private void initGui() {
		tabbedPane = new JTabbedPane();
		openedFilePaths = new ArrayList<>();
		emptyNameToolbarButtons = new ArrayList<>();
		
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				String newTitle;
				int index = tabbedPane.getSelectedIndex();
				if(index == -1) {
					setTitle(FRAME_NAME);
					return;
				}
				
				if((index >= openedFilePaths.size()) 
						|| (openedFilePaths.get(index) == null)) {
					newTitle = tabbedPane.getTitleAt(index);
				} else {
					newTitle = openedFilePaths.get(
							index).toString();
				}

				changeTitle(newTitle);
				updateStatusBar();
			}
		});
		
		tabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(tabbedPane.getSelectedIndex() == -1) {
					return;
				}
				
				if(e.getButton() == MouseEvent.BUTTON2) {
					closeCurrentTab();
				}
			}
		});
		
		this.getContentPane().setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
		
		setStatusBarTexts();
		flp.addLocalizationListener(new ILocalizationListener() {
			@Override
			public void localizationChanged() {
				localizeNewDocuments();
				
				setStatusBarTexts();
				updateStatusBar();
				emptyNameToolbarButtons.forEach(t -> t.setText(""));
			}
		});
		
		createActions();
		createToolbars();
		createMenus();
		
		initStatusPanel();
		add(statusPanel, BorderLayout.PAGE_END);
		updateStatusBar();
	}
	
	/**
	 * Sets status bar texts.
	 */
	private void setStatusBarTexts() {
		String is = " : ";
		lengthText = " "+flp.getString(LocalizationKeys.TOOLBAR_LENGTH_KEY)+is;
		lnText = " "+flp.getString(LocalizationKeys.TOOLBAR_LN_KEY)+is;
		colText = flp.getString(LocalizationKeys.TOOLBAR_COL_KEY)+is;
		selText = flp.getString(LocalizationKeys.TOOLBAR_SEL_KEY)+is;
	}
	
	/**
	 * Localizes names of currently opened <b>new</b> documents, if such are
	 * present.
	 */
	private void localizeNewDocuments() {
		if(textAreaIsNotAvailable()) {
			return;
		}
		
		String newDocName = flp.getString(
				LocalizationKeys.DEFAULT_DOCUMENT_NAME_KEY);
		
		
		for(int i = 0; i < tabbedPane.getComponentCount(); ++i) {
			if(openedFilePaths.get(i) == null) {
				String updatedName = newDocName + " "
						+ tabbedPane.getTitleAt(i).split(" ")[1];
				//refresh document name shown in frame title
				if(tabbedPane.getSelectedIndex() == i) {
					this.changeTitle(updatedName);
				}
				tabbedPane.setTitleAt(i, updatedName);
			}
		}
	}
	
	/**
	 * Initializes the status toolbar.
	 */
	private void initStatusPanel() {
		statusPanel = new JToolBar();
		statusPanel.setFloatable(true);
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setLayout(new GridLayout(1, 3));
		
		jlTime = new JLabel();
		jlTime.setHorizontalAlignment(SwingConstants.RIGHT);
		sbLength = new JLabel();
		sbLength.setBorder(BorderFactory.createEmptyBorder());
		sbLength.setBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
		sbLnColSel = new JLabel();
		sbLnColSel.setBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
		
		statusPanel.add(sbLength);
		statusPanel.add(sbLnColSel);
		statusPanel.add(jlTime);
		
		timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClock();
            }
        });
		timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.setInitialDelay(0);
        timer.start();
	}
	
	/**
	 * Updates the clock.
	 */
	private void updateClock() {
		jlTime.setText(formatter.format(new Date())+" ");
	}
	
	/**
	 * Updates the status bar.
	 */
	private void updateStatusBar() {
		JTextArea textArea = getCurrentTextArea();
		String resLengthText = lengthText;
		String resLnText = lnText;
		String resColText = colText;
		String resSelText = selText;
		if(textArea != null) {
			resLengthText += textArea.getDocument().getLength();
			resLnText += textArea.getLineCount();
			int caretPos = textArea.getCaretPosition();
			int lineOffset;
			try {
				lineOffset = textArea.getLineOfOffset(caretPos);
				resColText += (caretPos - textArea.getLineStartOffset(lineOffset));
			} catch (BadLocationException e1) {}
			String selectedText = textArea.getSelectedText();
			resSelText += selectedText != null ? selectedText.length() : "";
		}
		
		sbLength.setText(resLengthText);
		sbLnColSel.setText(resLnText+SPACING+resColText+SPACING+resSelText);
	}
	
	/**
	 * Adds an empty text area as a new tab.
	 * 
	 * @param name tab name
	 * @throws IOException if I/O error of some kind has occurred
	 */
	private void addTextAreaToTabbedPane(String name) throws IOException {
		JTextArea textArea = new JTextArea();
		addTextAreaToTabbedPane(name, textArea, null);
	}
	
	/**
	 * Adds new text area to tabbed pane of this frame.
	 * 
	 * @param name tab name
	 * @param textArea new text area
	 * @param path path to file that new tab shows
	 */
	private void addTextAreaToTabbedPane(String name, JTextArea textArea, 
			Path path) {
		tabbedPane.add(name, new JScrollPane(textArea));
		openedFilePaths.add(path);
		tabbedPane.setSelectedIndex(tabbedPane.getComponentCount() - 1);
		tabbedPane.setIconAt(tabbedPane.getSelectedIndex(),
				UNEDITED_ICON);
		tabbedPane.setToolTipTextAt(tabbedPane.getSelectedIndex(), name);
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				evaluateChange();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				evaluateChange();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {}
			
			private void evaluateChange() {
				tabbedPane.setIconAt(tabbedPane.getSelectedIndex(),
						EDITED_ICON);
			}
		});
		textArea.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				updateStatusBar();
				
				if(getCurrentTextArea().getSelectedText() != null) {
					setEnabledMenuItemsActions(true);
				} else {
					setEnabledMenuItemsActions(false);
				}
			}
		});
	}
	
	/**
	 * Closes the current tab.
	 * 
	 * @return <b>true</b> if tab is closed
	 */
	private boolean closeCurrentTab() {
		int index = tabbedPane.getSelectedIndex();
		if(index == -1) {
			return false;
		}
		if(tabbedPane.getIconAt(index) == EDITED_ICON) {
			int res = JOptionPane.showConfirmDialog(this, 
					flp.getString(LocalizationKeys.SAVE_KEY)+" \""
					+tabbedPane.getTitleAt(index)+"\" ?", 
					flp.getString(LocalizationKeys.SAVE_KEY), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if(res == JOptionPane.CANCEL_OPTION 
					|| res == JOptionPane.CLOSED_OPTION) {
				return false;
			}
			if(res == JOptionPane.YES_OPTION) {
				if(openedFilePaths.get(index) == null) {
					saveAsOption();
				}

				save();
			}
		}

		tabbedPane.remove(index);
		return true;
	}
	
	/**
	 * Closes all tabs.
	 * 
	 * @return <b>true</b> if all tabs are closed
	 */
	private boolean closeAllTabs() {
		if(tabbedPane.getSelectedIndex() == -1) {
			return true;
		}
		
		while(closeCurrentTab()) {
			tabbedPane.setSelectedIndex(tabbedPane.getComponentCount() - 1);
		};
		
		return (tabbedPane.getComponentCount() == 0) ? true : false;
	}
	
	/**
	 * Gets current text area path.
	 * 
	 * @return current text area path
	 */
	private Path getCurrentTextAreaPath() {
		return openedFilePaths.get(tabbedPane.getSelectedIndex());
	}
	
	/**
	 * Changes title of this frame.
	 * 
	 * @param name currently opened document's name, or null if frame should be
	 * set to default name
	 */
	private void changeTitle(String name) {
		setTitle(name != null ? (name + " - " + FRAME_NAME) : (FRAME_NAME));
	}
	
	/**
	 * Sets the path of currently opened document.
	 * 
	 * @param path path of currently opened document
	 */
	private void setCurrentTextAreaPath(Path path) {
		openedFilePaths.add(tabbedPane.getSelectedIndex(), path);
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(),
				path.getFileName().toString());
		changeTitle(path.toString());
	}
	
	/**
	 * Gets currently opened text area. If opened text area exists it is 
	 * returned and if it does not exist null is returned.
	 * 
	 * @return opened text area if available, <b>null</b> if not available
	 */
	private JTextArea getCurrentTextArea() {
		if(textAreaIsNotAvailable()) {
			return null;
		}
		
		JScrollPane jsp = (JScrollPane)tabbedPane.getComponentAt(
				tabbedPane.getSelectedIndex());
		return (JTextArea)jsp.getViewport().getView();
	}
	
	/**
	 * Checks if there are no available text areas.
	 * 
	 * @return <b>'true'</b> if there are no available text areas
	 */
	private boolean textAreaIsNotAvailable() {
		return tabbedPane.getSelectedIndex() == -1;
	}
	
	/**
	 * Gets image icon from given path.
	 * 
	 * @param path path to icon
	 * @return {@link ImageIcon}
	 */
	private ImageIcon getImageIcon(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		if(is == null) {
			return null;
		}
		byte[] bytes;
		try {
		bytes = readAllBytes(is);
		is.close();
		} catch(IOException ex) {
			return null;
		}
		return new ImageIcon(bytes); 
	}
	
	/**
	 * Reads all bytes from given {@link InputStream} and returns them as a byte
	 * array.
	 * 
	 * @param is input stream
	 * @return byte array representation of input stream
	 * @throws IOException if I/O error of some kind has occurred
	 */
	private byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true) {
		    int r = is.read(buffer);
		    if (r == -1) break;
		    out.write(buffer, 0, r);
		}

		byte[] ret = out.toByteArray();
		return ret;
	}
	
	/**
	 * Creates toolbars.
	 */
	private void createToolbars() {
		toolBar = new JToolBar();
		toolBar.setFloatable(true);
		
		toolBar.add(getToolbarButtonFor(newDocumentAction, 
				getImageIcon("icons/new.png")));
		toolBar.add(getToolbarButtonFor(openDocumentAction, 
				getImageIcon("icons/open.png")));
		toolBar.add(getToolbarButtonFor(saveDocumentAction, 
				getImageIcon("icons/save.png")));
		toolBar.add(getToolbarButtonFor(saveAsDocumentAction, 
				getImageIcon("icons/saveas.png")));
		toolBar.add(getToolbarButtonFor(closeDocumentAction, 
				getImageIcon("icons/close.png")));
		toolBar.addSeparator();
		toolBar.add(getToolbarButtonFor(cutDocumentAction, 
				getImageIcon("icons/cut.png")));
		toolBar.add(getToolbarButtonFor(copyDocumentAction, 
				getImageIcon("icons/copy.png")));
		toolBar.add(getToolbarButtonFor(pasteDocumentAction, 
				getImageIcon("icons/paste.png")));
		toolBar.addSeparator();
		toolBar.add(getToolbarButtonFor(statisticsDocumentAction, 
				getImageIcon("icons/statistics.png")));
		toolBar.addSeparator();
		toolBar.add(getToolbarButtonFor(exitAction, 
				getImageIcon("icons/exit.png")));
		
		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
	}
	
	/**
	 * Gets tool bar button for given action and icon.
	 * 
	 * @param action tool bar button action
	 * @param icon tool bar button icon
	 * @return tool bar button
	 */
	private JButton getToolbarButtonFor(Action action, ImageIcon icon) {
		JButton button = new JButton(action);
		button.setIcon(icon);
		button.setPreferredSize(new Dimension(icon.getIconWidth(),
				icon.getIconHeight()));
		button.setText("");
		button.setOpaque(false);
		emptyNameToolbarButtons.add(button);
		return button;
	}

	/**
	 * Creates menus.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu(
				getMenuActionFor(LocalizationKeys.FILE_KEY));
		menuBar.add(fileMenu);
		
		fileMenu.add(new JMenuItem(newDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(saveAsDocumentAction));
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.add(new JMenuItem(closeAllDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(exitAction));
		
		JMenu editMenu = new JMenu(
				getMenuActionFor(LocalizationKeys.EDIT_KEY));
		menuBar.add(editMenu);
		
		editMenu.add(new JMenuItem(cutDocumentAction));
		editMenu.add(new JMenuItem(copyDocumentAction));
		editMenu.add(new JMenuItem(pasteDocumentAction));
		
		JMenu toolsMenu = new JMenu(
				getMenuActionFor(LocalizationKeys.TOOLS_KEY));
		JMenu changeCaseMenu = new JMenu(
				getMenuActionFor(LocalizationKeys.CHANGE_CASE_KEY));
		toolsMenu.add(changeCaseMenu);
		toUpperCaseItem = new JMenuItem(toUpperCaseAction);
		toLowerCaseItem = new JMenuItem(toLowerCaseAction);
		invertCaseItem = new JMenuItem(invertCaseAction);
		changeCaseMenu.add(toUpperCaseItem);
		changeCaseMenu.add(toLowerCaseItem);
		changeCaseMenu.add(invertCaseItem);
		
		JMenu sortMenu = new JMenu(
				getMenuActionFor(LocalizationKeys.SORT_MENU_KEY));
		toolsMenu.add(sortMenu);
		sortAscendingItem = new JMenuItem(sortAscendingAction);
		sortDescendingItem = new JMenuItem(sortDescendingAction);
		sortMenu.add(sortAscendingItem);
		sortMenu.add(sortDescendingItem);
		uniqueLinesItem = new JMenuItem(uniqueLinesAction);
		
		toolsMenu.add(uniqueLinesAction);
		menuBar.add(toolsMenu);
		
		JMenu infoMenu = new JMenu(
				getMenuActionFor(LocalizationKeys.INFO_KEY));
		menuBar.add(infoMenu);
		
		infoMenu.add(new JMenuItem(statisticsDocumentAction));
		
		JMenu langMenu = new JMenu(
				getMenuActionFor(LocalizationKeys.LANGUAGES_KEY));
		langMenu.add(new JMenuItem(
				getLanguageChangeActionFor(
						LocalizationKeys.EN_LANG_KEY, 
						LocalizationLanguages.EN))
				);
		langMenu.add(new JMenuItem(
				getLanguageChangeActionFor(
						LocalizationKeys.HR_LANG_KEY, 
						LocalizationLanguages.HR))
				);
		langMenu.add(new JMenuItem(
				getLanguageChangeActionFor(
						LocalizationKeys.DE_LANG_KEY, 
						LocalizationLanguages.DE))
				);
		menuBar.add(langMenu);
		
		this.setJMenuBar(menuBar);
		setEnabledMenuItemsActions(false);
	}
	
	/**
	 * Sets menu items availability.
	 * 
	 * @param enabled <b>'true'</b> if menu items will be available
	 */
	private void setEnabledMenuItemsActions(boolean enabled) {
		toUpperCaseItem.setEnabled(enabled);
		toLowerCaseItem.setEnabled(enabled);
		invertCaseAction.setEnabled(enabled);
		sortAscendingItem.setEnabled(enabled);
		sortDescendingItem.setEnabled(enabled);
		uniqueLinesItem.setEnabled(enabled);
	}

	/**
	 * Creates actions.
	 */
	private void createActions() {
		openDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control 0"));
		openDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_0);
		
		saveDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control S"));
		saveDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		
		saveAsDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control alt S"));
		saveAsDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		
		cutDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control X"));
		cutDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		
		exitAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("alt F4"));
		exitAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F4);
		
		newDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control N"));
		newDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		
		copyDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control C"));
		copyDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		
		pasteDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control V"));
		pasteDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
		
		statisticsDocumentAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control I"));
		statisticsDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
		
		closeDocumentAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control W"));
		closeDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
	}
	
	/**
	 * Action which removes from selection all lines which are duplicates 
	 * (only the first occurrence is retained).
	 */
	private Action uniqueLinesAction = new LocalizableAction(
			LocalizationKeys.UNIQUE_LINES_KEY, null, flp) {
		
		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea textArea = getCurrentTextArea();
			if(textArea == null) {
				return;
			}
			String changeCase = textArea.getSelectedText();
			if(changeCase == null) {
				return;
			}
			List<String> lines = getLinesOfSelectedTextAndRemoveThem(textArea);
			if(lines == null) {
				return;
			}
			lines = lines.stream().distinct().collect(Collectors.toList());
			StringBuilder sb = new StringBuilder();
			lines.forEach(a -> sb.append(a).append(System.lineSeparator()));
			textArea.insert(sb.toString(), textArea.getCaretPosition());
		}
	};
	
	/**
	 * Action which transforms all characters of selected text to uppercase.
	 */
	private Action toUpperCaseAction = new LocalizableAction(
			LocalizationKeys.TO_UPPERCASE_KEY, 
			null,
			flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea textArea = getCurrentTextArea();
			if(textArea == null) {
				return;
			}
			String changeCase = textArea.getSelectedText();
			if(changeCase == null) {
				return;
			}
			textArea.replaceSelection(changeCase.toUpperCase());

		}
	};
	
	/**
	 * Action which sorts selected lines in ascending order.
	 */
	private Action sortAscendingAction = new LocalizableAction(
			LocalizationKeys.SORT_ASCENDING_KEY, null, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea area = getCurrentTextArea();
			if(area == null || area.getSelectedText() == null) {
				return;
			}

			Locale curLocale = LocalizationProvider.getInstance().getLocale();
			Collator hrCollator = Collator.getInstance(curLocale);
			List<String> lines = getLinesOfSelectedTextAndRemoveThem(area);
			if(lines == null) {
				return;
			}
			lines = lines.stream().sorted((a,b) -> hrCollator.compare(a, b))
					.collect(Collectors.toList());
			StringBuilder sb = new StringBuilder();
			lines.forEach(t -> sb.append(t).append(System.lineSeparator()));
			area.insert(sb.toString(), area.getCaretPosition());
		}
	};
	
	/**
	 * Action which sorts selected lines in descending order.
	 */
	private Action sortDescendingAction = new LocalizableAction(
			LocalizationKeys.SORT_DESCENDING_KEY, null, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea area = getCurrentTextArea();
			if(area == null || area.getSelectedText() == null) {
				return;
			}

			Locale curLocale = LocalizationProvider.getInstance().getLocale();
			Collator hrCollator = Collator.getInstance(curLocale);
			List<String> lines = getLinesOfSelectedTextAndRemoveThem(area);
			try {
			lines = lines.stream().sorted((a,b) -> -hrCollator.compare(a, b))
					.collect(Collectors.toList());
			} catch(NullPointerException ex) {
			}
			StringBuilder sb = new StringBuilder();
			lines.forEach(t -> sb.append(t).append(System.lineSeparator()));
			area.insert(sb.toString(), area.getCaretPosition());
		}
	};
	
	/**
	 * Gets lines of selected text and removes them from given 
	 * {@link JTextArea}. Starting line is one in which the start dot of 
	 * selected text is positioned, and the end line is one in which the end 
	 * dot of selected text is.
	 * 
	 * @param area area in which text is selected
	 * @return {@link List} of selected text's lines
	 */
	private List<String> getLinesOfSelectedTextAndRemoveThem(JTextArea area) {
		int selectedStart = area.getSelectionStart();
		int selectedEnd = area.getSelectionEnd();
		if(selectedStart == selectedEnd) {
			return null;
		}
		
		ArrayList<String> res = new ArrayList<>();
		try {
		int lineNumber1 = area.getLineOfOffset(selectedStart);
		int lineNumber2 = area.getLineOfOffset(selectedEnd);
		int startOffset = area.getLineStartOffset(lineNumber1);
		int endOffset = area.getLineEndOffset(lineNumber2);
		
		String text = area.getText().substring(startOffset, endOffset);
		area.getDocument().remove(startOffset, endOffset);
		Collections.addAll(res, text.split("(\\r)?\\n"));
		} catch (BadLocationException e) {
			return null;
		}
		
		return res;
	}
	
	/**
	 * Action which transforms selected text's characters to lower case.
	 */
	private Action toLowerCaseAction = new LocalizableAction(
			LocalizationKeys.TO_LOWERCASE_KEY, 
			null,
			flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
					JTextArea textArea = JNotepadPP.this.getCurrentTextArea();
					if(textArea == null) {
						return;
					}
					String changeCase = textArea.getSelectedText();
					if(changeCase == null) {
						return;
					}
					textArea.replaceSelection(changeCase.toLowerCase());
		}
		
	};
	
	/**
	 * Action which inverts characters of selected text.
	 */
	private Action invertCaseAction = new LocalizableAction(
			LocalizationKeys.INVERTCASE_KEY, 
			null,
			flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea textArea = JNotepadPP.this.getCurrentTextArea();
			if(textArea == null) {
				return;
			}
			String changeCase = textArea.getSelectedText();
			if(changeCase == null) {
				return;
			}
			textArea.replaceSelection(invertCase(changeCase));
		}
		
		/**
		 * Inverts given text's characters.
		 * 
		 * @param text text 
		 * @return string which represents given text's inverted characters
		 */
		private String invertCase(String text) {
			char[] textChars = text.toCharArray();
			
			for(int i = 0; i < textChars.length; ++i) {
				char curChar = textChars[i];
				
				if(Character.isUpperCase(curChar)) {
					curChar = Character.toLowerCase(curChar);
				} else if(Character.isLowerCase(curChar)) {
					curChar = Character.toUpperCase(curChar);
				}
				
				textChars[i] = curChar;
			}
			
			return new String(textChars);
		}
	};

	/**
	 * Gets basic menu action for given localization key. 
	 * 
	 * @param key localization key
	 * @return {@link Action} for given key
	 */
	private Action getMenuActionFor(String key) {
		return new LocalizableAction(key, null, flp) {

			/**
			 * The default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {}
		};
	}
	
	/**
	 * Gets language change action for given name key and language key.
	 * 
	 * @param key name key
	 * @param lang language key
	 * @return {@link Action} for given keys
	 */
	private Action getLanguageChangeActionFor(String key, String lang) {
		return new LocalizableAction(key, null, flp) {

			/**
			 * The default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LocalizationProvider.getInstance().setLanguage(
						lang);
			}
		};
	}


	/**
	 * Action which opens a document.
	 */
	private Action openDocumentAction =
			new LocalizableAction(LocalizationKeys.OPEN_KEY, 
					LocalizationKeys.OPEN_SHORTDESC_KEY, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(flp.getString(LocalizationKeys.OPEN_FILE_KEY));
			if(fc.showOpenDialog(JNotepadPP.this)!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();
			if(!Files.isReadable(filePath)) {
				JOptionPane.showMessageDialog(
						JNotepadPP.this, 
						flp.getString(LocalizationKeys.FILE_KEY) +" " 
						+ fileName.getAbsolutePath() + " "
						+flp.getString(LocalizationKeys.DOES_NOT_EXIST_KEY)+"!", 
						flp.getString(LocalizationKeys.ERROR_KEY), 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			byte[] bytes;
			try {
				bytes = Files.readAllBytes(filePath);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(JNotepadPP.this, 
						flp.getString(
								LocalizationKeys.FILE_READING_ERROR_MSG_KEY)
								+" "+ fileName.getAbsolutePath(), 
								flp.getString(LocalizationKeys.ERROR_KEY), 
								JOptionPane.ERROR_MESSAGE);
				return;
			}
			String text = new String(bytes, StandardCharsets.UTF_8);
			JTextArea textArea = new JTextArea(text);
			addTextAreaToTabbedPane(fileName.getName(), textArea, filePath);
		}
	};

	/**
	 * Action which saves a document.
	 */
	private Action saveDocumentAction = 
			new LocalizableAction(LocalizationKeys.SAVE_KEY,
					LocalizationKeys.SAVE_SHORTDESC_KEY, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(textAreaIsNotAvailable()) {
				return;
			}
			
			if(getCurrentTextAreaPath() == null) {
				if(!saveAsOption()) {
					return;
				}
			}
			save();
		}
		
	};
	
	/**
	 * Saves the currently opened document.
	 */
	private void save() {
		byte[] bytes = getCurrentTextArea().getText().getBytes(
				StandardCharsets.UTF_8);
		int curTabIndex = tabbedPane.getSelectedIndex();
		try {
			Files.write(getCurrentTextAreaPath(), bytes);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(JNotepadPP.this, 
					flp.getString(LocalizationKeys.FILE_SAVING_ERROR_MSG_KEY)
						+"."+ getCurrentTextAreaPath().toString(), 
							flp.getString(LocalizationKeys.ERROR_KEY), 
							JOptionPane.ERROR_MESSAGE);
			/*If error occurs, set path of current document to null for
			safety reasons.*/
			openedFilePaths.add(curTabIndex, null);
			return;
		}
		tabbedPane.setIconAt(curTabIndex, UNEDITED_ICON);
		Path filePath = openedFilePaths.get(curTabIndex);
		tabbedPane.setTitleAt(
				curTabIndex, filePath.getFileName().toString());
		tabbedPane.setToolTipTextAt(
				curTabIndex, filePath.toString());
		
		JOptionPane.showMessageDialog(
				JNotepadPP.this, 
				flp.getString(LocalizationKeys.FILE_SAVED_MSG_KEY)+".", 
				flp.getString(LocalizationKeys.INFORMATION_KEY), 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Action which saves current document on new location on disk.
	 */
	private Action saveAsDocumentAction = 
			new LocalizableAction(LocalizationKeys.SAVE_AS_KEY, 
					LocalizationKeys.SAVE_AS_SHORTDESC_KEY, flp) {
		
		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(saveAsOption()) {
				save();
			}
		}
	};
	
	/**
	 * Asks user to select the saving location of current element. 
	 * 
	 * @return <b>'true'</b> if the saving location is determined and is valid
	 */
	private boolean saveAsOption() {
		if(textAreaIsNotAvailable()) {
			return false;
		}
		while(true) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle(flp.getString(LocalizationKeys.SAVE_AS_KEY));
			if(jfc.showSaveDialog(JNotepadPP.this)!=
					JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString(LocalizationKeys.SAVING_WARNING_KEY)+".", 
						flp.getString(LocalizationKeys.WARNING_KEY), 
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(jfc.getSelectedFile().exists()) {
				int ans = JOptionPane.showConfirmDialog(JNotepadPP.this,
						flp.getString(LocalizationKeys.OVERWRITE_OPTION_KEY)
						+"?");

				if(ans == JOptionPane.YES_OPTION) {
					setCurrentTextAreaPath(jfc.getSelectedFile().toPath());
					break;
				} else if(ans == JOptionPane.CANCEL_OPTION) {
					return false;
				}
			} else {
				setCurrentTextAreaPath(jfc.getSelectedFile().toPath());
				break;
			}
		}

		return true;
	}
	
	/**
	 * Cuts the selected part of document.
	 */
	private Action cutDocumentAction = 
			new LocalizableAction(LocalizationKeys.CUT_KEY, 
					LocalizationKeys.CUT_SHORTDESC_KEY, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(textAreaIsNotAvailable()) {
				return;
			}
			
			getCurrentTextArea().cut();
		}
	};
	
	/**
	 * Action which exits the application.
	 */
	private Action exitAction = 
			new LocalizableAction(LocalizationKeys.EXIT_KEY, 
					LocalizationKeys.EXIT_SHORTDESC_KEY, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			exit();
		}
	};
	
	/**
	 * Exits the frame.
	 */
	private void exit() {
		if(closeAllTabs()) {
			dispose();
		}
	}
	
	/**
	 * Action which creates a new document.
	 */
	private Action newDocumentAction = 
			new LocalizableAction(LocalizationKeys.NEW_KEY,
					LocalizationKeys.NEW_SHORTDESC_KEY, flp) {
		
		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int curIndex = 0;
			if(!textAreaIsNotAvailable()) {
				int totalTabs = tabbedPane.getComponentCount();
				for(int i = 0; i < totalTabs; ++i) {
					if(openedFilePaths.get(i) == null) {
						String tabName = tabbedPane.getTitleAt(i);
						int index = Integer.parseInt(tabName.split(" ")[1]);
						curIndex = curIndex < index ? index : curIndex;
					}
				}
			}

			try {
				addTextAreaToTabbedPane(
						flp.getString(
								LocalizationKeys.DEFAULT_DOCUMENT_NAME_KEY)
						+" "+(curIndex+1));
			} catch (IOException e1) {
			}
		}
	};
	
	/**
	 * Action which copies currently selected text.
	 */
	private Action copyDocumentAction = 
			new LocalizableAction(LocalizationKeys.COPY_KEY,
					LocalizationKeys.COPY_SHORTDESC_KEY, flp) {
		
		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(textAreaIsNotAvailable()) {
				return;
			}
			getCurrentTextArea().copy();
		}
	};
	
	/**
	 * Action which pastes copied text on current document.
	 */
	private Action pasteDocumentAction = 
			new LocalizableAction(LocalizationKeys.PASTE_KEY,
					LocalizationKeys.PASTE_SHORTDESC_KEY, flp) {
		
		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(textAreaIsNotAvailable()) {
				return;
			}
			getCurrentTextArea().paste();
		}
	};

	/**
	 * Action which closes current document.
	 */
	private Action closeDocumentAction = 
			new LocalizableAction(LocalizationKeys.CLOSE_KEY,
					LocalizationKeys.CLOSE_SHORTDESC_KEY, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			closeCurrentTab();
		}
	};
	
	/**
	 * Action which closes all currently opened documents.
	 */
	private Action closeAllDocumentAction = 
			new LocalizableAction(LocalizationKeys.CLOSE_ALL_KEY,
					LocalizationKeys.CLOSE_ALL_SHORTDESC_KEY, flp) {

		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			closeAllTabs();
		}
	};
	
	/**
	 * Action which calculates the statistics of currently opened document.
	 */
	private Action statisticsDocumentAction = 
			new LocalizableAction(LocalizationKeys.STATS_KEY,
					LocalizationKeys.STATS_SHORTDESC_KEY, flp) {
		
		/**
		 * The default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea curTextArea = getCurrentTextArea();
			if(curTextArea == null) {
				return;
			}
			int allChars = curTextArea.getDocument().getLength();
			int nonBlankChars = curTextArea.getText()
					.replaceAll("\\s+", "").length();
			int lines = curTextArea.getLineCount();
			
			String message = 
					flp.getString(LocalizationKeys.STATS_OUT_PARTONE_KEY)+" "
					+allChars+" "
					+flp.getString(LocalizationKeys.STATS_OUT_PARTTWO_KEY)+", "
					+nonBlankChars+" "
					+flp.getString(LocalizationKeys.STATS_OUT_PARTTHREE_KEY)
					+" "+lines+" "
					+flp.getString(LocalizationKeys.STATS_OUT_PARTFOUR_KEY)+".";
			JOptionPane.showMessageDialog(JNotepadPP.this,
					message, 
					(String)this.getValue(Action.NAME),
					JOptionPane.INFORMATION_MESSAGE);
		}
	};
	
	/**
	 * The main method. Runs when program is started. 
	 * <p>Initializes the {@link JNotepadPP} frame.</p>
	 * 
	 * @param args arguments from the command line, not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new JNotepadPP().setVisible(true);
		});
	}
	
}
