package simplejavatexteditor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

public class AutoCorrect implements DocumentListener{
	private final UI ui;
	private final JTextArea textArea;
	private int pos;
	private String content;
	private String[] wordsArray;
	private Highlighter highlighter;
	private HighlightPainter painterCyan;
	
	public AutoCorrect(UI ui) {
        //Access the editor
        this.ui = ui;
        textArea = ui.getEditor();
        highlighter = textArea.getHighlighter();
        painterCyan = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
        //Set the handler for the enter key
        //InputMap im = textArea.getInputMap();
        //ActionMap am = textArea.getActionMap();
        //im.put(KeyStroke.getKeyStroke("ENTER "), COMMIT_ACTION);
        //am.put(COMMIT_ACTION, new CommitAction());
    }
	
	/**
     * A character has been typed into the document.
     * This method performs the primary
     * check to find a action to complete
     *
     * @param e
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
    	pos = e.getOffset();
    	
    	try {
            content = textArea.getText(0, pos + 1);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        if (e.getLength() != 1) {
            //return;
        }
        
    	//only perform checks after a space
    	char c = content.charAt(pos);
        if(c != ' '){
        	return;
        }
        
        //keep an array of entered words
        wordsArray = textArea.getText().split("\\s+");
        checkDouble();
    }
    
    private void checkDouble(){
        //Get the beginning of the last word typed
        int start;
        for (start = (pos-1); start >= 0; start--) {
            if (!Character.isLetter(content.charAt(start))) {
                break;
            }
        }
        
        if(wordsArray.length<2)
        	return;
        
        for(int i=0; i<(wordsArray.length-1); i++){
        	String lastWord = wordsArray[i + 1].replaceAll("[^a-zA-Z]", "").toLowerCase();;
            String lastlastWord = wordsArray[i].replaceAll("[^a-zA-Z]", "").toLowerCase();;
	        if(lastWord.equals(lastlastWord)){
	            String wordToFind = wordsArray[i] + " " +  wordsArray[i + 1];
	            System.out.println(wordToFind);
	            try{
	            	Pattern word = Pattern.compile(wordToFind);
	            	Matcher match = word.matcher(content);
		        	if(match.find())
		        		SwingUtilities.invokeLater(new HighlightTask(match.start(), match.end()));
	            } catch (PatternSyntaxException ex) {
	                ex.printStackTrace();
	            }
	        }
        }     
    }
    
    private class HighlightTask implements Runnable{
    	
    	private final int start, end;
    	public HighlightTask(int start, int end){
    		this.start = start;
    		this.end = end;
    	}
    	
        @Override
        public void run() {
        	try{
        		highlighter.addHighlight(start, end, painterCyan);
        	} catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
	
    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    	
    }
    
}
