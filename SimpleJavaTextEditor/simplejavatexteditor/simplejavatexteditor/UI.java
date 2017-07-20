/**
 * @name        Simple Java NotePad
 * @package     ph.notepad
 * @file        UI.java
 *
 * @author      Pierre-Henry Soria
 * @email       pierrehenrysoria@gmail.com
 * @link        http://github.com/pH-7
 *
 * @copyright   Copyright Pierre-Henry SORIA, All Rights Reserved.
 * @license     Apache (http://www.apache.org/licenses/LICENSE-2.0)
 * @create      2012-04-05
 * @update      2017-02-18
 *
 * @modifiedby  Achintha Gunasekara
 * @modemail    contact@achinthagunasekara.com
*
 * @modifiedby  Marcus Redgrave-Close
 * @modemail    marcusrc1@hotmail.co.uk
 */

package simplejavatexteditor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.text.DefaultEditorKit;
import javax.swing.border.BevelBorder;

public class UI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Container container;
    private final JTextArea textArea;
    private final JScrollPane scrolll;
    private final JMenuBar menuBar;
    private final JComboBox fontSize, fontType;
    private final JMenu menuFile, menuEdit, menuFind, menuAbout;
    private final JMenuItem newFile, openFile, saveFile, close, cut, copy, paste, clearFile, selectAll, quickFind,
            aboutMe, aboutSoftware, wordWrap, appearenceSettings, lightMode, darkMode;
    //private final JToolBar mainToolbar;
    //Button newButton, openButton, saveButton, clearButton, quickButton, aboutMeButton, aboutButton, closeButton;
    private final Action selectAllAction;



    // setup icons - File Menu
    private final ImageIcon newIcon = new ImageIcon("icons/new.png");
    private final ImageIcon openIcon = new ImageIcon("icons/open.png");
    private final ImageIcon saveIcon = new ImageIcon("icons/save.png");
    private final ImageIcon closeIcon = new ImageIcon("icons/close.png");

    // setup icons - Edit Menu
    private final ImageIcon clearIcon = new ImageIcon("icons/clear.png");
    private final ImageIcon cutIcon = new ImageIcon("icons/cut.png");
    private final ImageIcon copyIcon = new ImageIcon("icons/copy.png");
    private final ImageIcon pasteIcon = new ImageIcon("icons/paste.png");
    private final ImageIcon selectAllIcon = new ImageIcon("icons/selectall.png");
    private final ImageIcon wordwrapIcon = new ImageIcon("icons/wordwrap.png");

    // setup icons - Search Menu
    private final ImageIcon searchIcon = new ImageIcon("icons/search.png");

    // setup icons - Help Menu
    private final ImageIcon aboutMeIcon = new ImageIcon("icons/about_me.png");
    private final ImageIcon aboutIcon = new ImageIcon("icons/about.png");

    AutoComplete autocomplete;
    private boolean hasListener = false;


    public UI()
    {
        container = getContentPane();

        // Set the initial size of the window
        setSize(700, 500);

        // Set the title of the window
        setTitle("Untitled | " + SimpleJavaTextEditor.NAME);

        // Set the default close operation (exit when it gets closed)
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set a default font for the TextArea
        textArea = new JTextArea("", 0,0);
        textArea.setFont(new Font("Century Gothic", Font.BOLD, 12));
        textArea.setTabSize(2);
        textArea.setFont(new Font("Century Gothic", Font.BOLD, 12));
        textArea.setTabSize(2);
     
        // Set up scroll bar
        scrolll = new JScrollPane(textArea);
        scrolll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrolll.setPreferredSize(new Dimension(250, 145));
        scrolll.setMinimumSize(new Dimension(10, 10));
        
        
        
        /* SETTING BY DEFAULT WORD WRAP ENABLED OR TRUE */
        textArea.setLineWrap(true);

        // This is why we didn't have to worry about the size of the TextArea!
        getContentPane().setLayout(new BorderLayout()); // the BorderLayout bit makes it fill it automatically
        getContentPane().add(scrolll);


        // Set the Menus
        menuFile = new JMenu("File");
        menuEdit = new JMenu("Edit");
        menuFind = new JMenu("Search");
        menuAbout = new JMenu("About");
        //Font Settings menu

        // Set the Items Menu
        newFile = new JMenuItem("New", newIcon);
        openFile = new JMenuItem("Open", openIcon);
        saveFile = new JMenuItem("Save", saveIcon);
        close = new JMenuItem("Quit", closeIcon);
        clearFile = new JMenuItem("Clear", clearIcon);
        quickFind = new JMenuItem("Quick", searchIcon);
        aboutMe = new JMenuItem("About Me", aboutMeIcon);
        aboutSoftware = new JMenuItem("About Software", aboutIcon);


        menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuFind);
        menuBar.add(menuAbout);
        this.setJMenuBar(menuBar);
        

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getContentPane().getWidth(), 19));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
       
        // Create box and populate entries
        Box statusBox = Box.createHorizontalBox();
        JLabel inputStatusLabel = new JLabel("--Insert--");
        JLabel filenameLabel = new JLabel("Filename");
        JLabel sourceTypeLabel = new JLabel("Plaintext Mode");
        JLabel cursorPosLabel = new JLabel("X,Y");
        JLabel scrolledPercentLabel = new JLabel("50%");
        
        // Add entries and set visible
        statusBox.add(inputStatusLabel);
        statusBox.add(Box.createGlue());
        statusBox.add(filenameLabel);
        statusBox.add(Box.createGlue());
        statusBox.add(sourceTypeLabel);
        statusBox.add(Box.createGlue());
        statusBox.add(cursorPosLabel);
        statusBox.add(Box.createRigidArea(new Dimension(10,0)));
        statusBox.add(scrolledPercentLabel);
        statusPanel.add(statusBox);
        getContentPane().setVisible(true);

        // Set Actions:
        selectAllAction = new SelectAllAction("Select All", clearIcon, "Select all text", new Integer(KeyEvent.VK_A),textArea);
        this.setJMenuBar(menuBar);

        // New File
        newFile.addActionListener(this);  // Adding an action listener (so we know when it's been clicked).
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK)); // Set a keyboard shortcut
        menuFile.add(newFile); // Adding the fileAnother item menu

        // Open File
        openFile.addActionListener(this);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        menuFile.add(openFile);

        // Save File
        saveFile.addActionListener(this);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        menuFile.add(saveFile);

        // Close File
        /*
         * Along with our "CTRL+F4" shortcut to close the window, we also have
         * the default closer, as stated at the beginning of this tutorial. this
         * means that we actually have TWO shortcuts to close:
         * 1) the default close operation (example, Alt+F4 on Windows)
         * 2) CTRL+F4, which we are
         * about to define now: (this one will appear in the label).
         */
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        close.addActionListener(this);
        menuFile.add(close);

        // Select All Text
        selectAll = new JMenuItem(selectAllAction);
        selectAll.setText("Select All");
        selectAll.setIcon(selectAllIcon);
        selectAll.setToolTipText("Select All");
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        menuEdit.add(selectAll);

        // Clear File (Code)
        clearFile.addActionListener(this);
        clearFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
        menuEdit.add(clearFile);

        // Cut Text
        cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setIcon(cutIcon);
        cut.setToolTipText("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuEdit.add(cut);

        // WordWrap
        wordWrap = new JMenuItem();
        wordWrap.setText("Word Wrap");
        wordWrap.setIcon(wordwrapIcon);
        wordWrap.setToolTipText("Word Wrap");

        //Short cut key or key stroke
        wordWrap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        menuEdit.add(wordWrap);

        /* CODE FOR WORD WRAP OPERATION
         * BY DEFAULT WORD WRAPPING IS ENABLED.
        */
        wordWrap.addActionListener(new ActionListener()
        {
                public void actionPerformed(ActionEvent ev) {
                    // If wrapping is false then after clicking on menuitem the word wrapping will be enabled
                    if(textArea.getLineWrap()==false) {
                        /* Setting word wrapping to true */
                        textArea.setLineWrap(true);
                    } else {
                        // else  if wrapping is true then after clicking on menuitem the word wrapping will be disabled
                        /* Setting word wrapping to false */
                        textArea.setLineWrap(false);
                }
            }
        });

        // Copy Text
        copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setIcon(copyIcon);
        copy.setToolTipText("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuEdit.add(copy);

        // Paste Text
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setIcon(pasteIcon);
        paste.setToolTipText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        menuEdit.add(paste);
        
        //Font Text
        appearenceSettings = new JMenu("Appearence");
        //fontSettings.setMnemonic(KeyEvent.VK_S);
        lightMode = new JMenuItem("Light");
        //size.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        appearenceSettings.add(lightMode);
        darkMode = new JMenuItem("Dark");
        appearenceSettings.add(darkMode);
        menuEdit.add(appearenceSettings);
        
        // Find Word
        quickFind.addActionListener(this);
        quickFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        menuFind.add(quickFind);

        // About Me
        aboutMe.addActionListener(this);
        aboutMe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuAbout.add(aboutMe);

        // About Software
        aboutSoftware.addActionListener(this);
        aboutSoftware.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        menuAbout.add(aboutSoftware);


  /****************** FONT SETTINGS SECTION **********************Another item*/

        //FONT FAMILY SETTINGS SECTION START
        fontType = new JComboBox();

          //GETTING ALL AVAILABLAnother itemE FONT FOMILY NAMES
        String [] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (int i = 0; i < fonts.length; i++)
        {
            //Adding font family names to font[] array
             fontType.addItem ( fonts [i] );
        }
        //Setting maximize size of the fontType ComboBox
        fontType.setMaximumSize( new Dimension ( 170, 20 ));
        menuBar.add(Box.createHorizontalGlue());	//pad to the right
        menuBar.add(fontType);
        //cursorPosLabel.mainToolbar.addSeparator();
      
        //Adding Action Listener on fontType JComboBox

        fontType.addActionListener(new ActionListener()
        {
                public void actionPerformed(ActionEvent ev)
                {
                    //Getting the selected fontType value from ComboBox
                    String p = fontType.getSelectedItem().toString();
                    //Getting size of the current font or text
                    int s = textArea.getFont().getSize();
                    textArea.setFont( new Font( p, Font.PLAIN, s));
                }
        });
        //FONT FAMILY SETTINGS SECTION END


        //FONT SIZE SETTINGS START
        fontSize = new JComboBox();

            for( int i = 5 ; i <= 100 ; i++)
            {
                fontSize.addItem( i );
            }
        fontSize.setMaximumSize( new Dimension( 70,20 ));
        menuBar.add( fontSize );

        fontSize.addActionListener(new ActionListener()
        {
                public void actionPerformed(ActionEvent ev)
                {
                   String sizeValue = fontSize.getSelectedItem().toString();
                    int sizeOfFont = Integer.parseInt( sizeValue );
                    String fontFamily = textArea.getFont().getFamily();

                    Font font1 = new Font( fontFamily , Font.PLAIN , sizeOfFont );
                    textArea.setFont( font1 );

                }
        });
        //FONT SIZE SETTINGS SECTION END
    }



    // Make the TextArea available to the autocomplete handler
    protected JTextArea getEditor() {
        return textArea;
    }

    public void actionPerformed (ActionEvent e) {
        // If the source of the event was our "close" option
        if (e.getSource() == close) {
            this.dispose(); // dispose all resources and close the application
        }
        // If the source was the "new" file option
        else if (e.getSource() == newFile) {
            FEdit.clear(textArea);
        }

        // If the source was the "open" option
        else if (e.getSource() == openFile) {
            JFileChooser open = new JFileChooser(); // open up a file chooser (a dialog for the user to  browse files to open)
            int option = open.showOpenDialog(this); // get the option that the user selected (approve or cancel)

            /*
             * NOTE: because we are OPENing a file, we call showOpenDialog~ if
             * the user clicked OK, we have "APPROVE_OPTION" so we want to open
             * the file
             */
            if (option == JFileChooser.APPROVE_OPTION) {
                FEdit.clear(textArea); // clear the TextArea before applying the file contents
                try {
                    // create a scanner to read the file (getSelectedFile().getPath() will get the path to the file)
                    Scanner scan = new Scanner(new FileReader(open.getSelectedFile().getPath()));
                    while (scan.hasNext()) // while there's still something to
                                            // read
                        textArea.append(scan.nextLine() + "\n"); // append the line to the TextArea
                } catch (Exception ex) { // catch any exceptions, and...
                    // ...write to the debug console
                    System.out.println(ex.getMessage());
                }
            }
        }
        // If the source of the event was the "save" option
        else if (e.getSource() == saveFile) {
            // Open a file chooser
            JFileChooser fileChoose = new JFileChooser();
            // Open the file, only this time we call
            int option = fileChoose.showSaveDialog(this);

            /*
             * ShowSaveDialog instead of showOpenDialog if the user clicked OK
             * (and not cancel)
             */
            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChoose.getSelectedFile();
                    // Set the new title of the window
                    setTitle(file.getName() + " | " + SimpleJavaTextEditor.NAME);
                    // Create a buffered writer to write to a file
                    BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath()));
                    // Write the contents of the TextArea to the file
                    out.write(textArea.getText());
                    // Close the file stream
                    out.close();

                    //If the user saves files with supported
                    //file types more than once, we need to remove
                    //previous listeners to avoid bugs.
                    if(hasListener) {
                        textArea.getDocument().removeDocumentListener(autocomplete);
                        hasListener = false;
                    }

                    //With the keywords located in a separate class,
                    //we can support multiple languages and not have to do
                    //much to add new ones.
                    SupportedKeywords kw = new SupportedKeywords();
                    ArrayList<String> arrayList;
                    String[] list = { ".java", ".cpp" };

                    //Iterate through the list, find the supported
                    //file extension, apply the appropriate getter method from
                    //the keyword class
                    for(int i = 0; i < list.length; i++) {
                        if(file.getName().endsWith(list[i])) {
                            switch(i) {
                                case 0:
                                    String[] jk = kw.getJavaKeywords();
                                    arrayList = kw.setKeywords(jk);
                                    autocomplete = new AutoComplete(this, arrayList);
                                    textArea.getDocument().addDocumentListener(autocomplete);
                                    hasListener = true;
                                    break;
                                case 1:
                                    String[] ck = kw.getCppKeywords();
                                    arrayList = kw.setKeywords(ck);
                                    autocomplete = new AutoComplete(this, arrayList);
                                    textArea.getDocument().addDocumentListener(autocomplete);
                                    hasListener = true;
                                    break;
                            }
                        }
                    }
                } catch (Exception ex) { // again, catch any exceptions and...
                    // ...write to the debug console
                    System.out.println(ex.getMessage());
                }
            }
        }

        // Clear File (Code)
        if (e.getSource() == clearFile) {
            FEdit.clear(textArea);
        }
        // Find
        if (e.getSource() == quickFind) {
            new Find(textArea);
        }

        // About Me
        else if (e.getSource() == aboutMe) {
            new About().me();
        }
        // About Software
        else if (e.getSource() == aboutSoftware) {
            new About().software();
        }

    }

    class SelectAllAction extends AbstractAction {
        /**
         * Used for Select All function
         */
        private static final long serialVersionUID = 1L;

        public SelectAllAction(String text, ImageIcon icon, String desc, Integer mnemonic, final JTextArea textArea) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e) {
            textArea.selectAll();
        }
    }
}
