package simplejavatexteditor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Scanner;

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
	private Map<String, Integer> map;
	
	public AutoCorrect(UI ui) {
        //Access the editor
        this.ui = ui;
        textArea = ui.getEditor();
        highlighter = textArea.getHighlighter();
        painterCyan = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
        getWordCounts();
    }
	
	private void getWordCounts() {
		ArrayList<String> words = new ArrayList<String>();
		
		//add ALL words to a list
		File file = new File("corpus");
	    try {
	        Scanner sc = new Scanner(file);
	        
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	           
	            Pattern p = Pattern.compile("\\b[\\w']+\\b");
	            Matcher m = p.matcher(line);
	            //generate word list
	            while ( m.find() ) {
	                words.add(line.substring(m.start(), m.end()).toLowerCase());
	            }
	        }
	        sc.close();
	        
	        //generate frequency map
	        this.map = new HashMap<>();
	        for (int i=0; i < words.size(); i++) {
	            Integer n = map.get(words.get(i));
	            n = (n == null) ? 1 : ++n;
	            map.put(words.get(i), n);
	        }
	        
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    
	    
	}
	
	//search for 1 edit fixes
	private String editDistance(String word) {
		int length = word.length();
		StringBuilder sb;
		String[] replacements = new String[1024];
		int[] likeliness = new int[1024];
		int index = 0;
		
		//delete any character
		for(int i=0; i<length; i++) {
			sb = new StringBuilder(word);
			sb.deleteCharAt(i);
			if(map.get(sb.toString())!=null) {
				replacements[index] = sb.toString();
				likeliness[index++] = map.get(sb.toString());
			}
			//System.out.println(sb.toString());
		}
		
		//add any character
		for(int i=0; i<length+1; i++) {
			sb = new StringBuilder(word);
			sb.insert(i, 'c');
			if(map.get(sb.toString())!=null) {
				replacements[index] = sb.toString();
				likeliness[index++] = map.get(sb.toString());
			}
			//System.out.println(sb.toString());
		}
		
		//rotate any two adjacent characters
		for(int i=0; i<length-1; i++) {
			sb = new StringBuilder(word);
			char tmp = sb.charAt(i);
			sb.setCharAt(i, sb.charAt(i+1));
			sb.setCharAt(i+1, tmp);
			if(map.get(sb.toString())!=null) {
				replacements[index] = sb.toString();
				likeliness[index++] = map.get(sb.toString());
			}
			//System.out.println(sb.toString());
		}
		
		//swap any one character
		for(int i=0; i<length; i++) {
			sb = new StringBuilder(word);
			for(char a = 'a'; a <= 'z'; a++) {
				sb.setCharAt(i, a);
				if(map.get(sb.toString())!=null) {
					replacements[index] = sb.toString();
					likeliness[index++] = map.get(sb.toString());
				}
				//System.out.println(sb.toString());
			}
		}
		
		String substitute = new String();
		int odds = 0;
		while(index-->0) {
			//System.out.println(replacements[index] + ", " + likeliness[index]);
			if(likeliness[index]>=odds) {
				substitute = replacements[index];
				odds = likeliness[index];
			}
		}
		if(odds == 0) return null;
		else return substitute;
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

        //run autocorrect
        String word = wordsArray[wordsArray.length-1].toLowerCase();
        String replace = editDistance(word);
        if(map.get(word)==null) {
        	System.out.println("incorrect word detected");
        	if(editDistance(word)==null)
        		System.out.println("no replacement found");
        	else {
        		System.out.println("Choose: " + replace);
        		int start = pos-word.length();
        		int end = start + word.length();
        		System.out.println(start + ", " + end);
        		SwingUtilities.invokeLater(new ReplaceTask(replace, start, end));
        	}
        }
        
        //check dupes
        checkDouble((pos - word.length()));
    }
    
    private void checkDouble(int start){
        //skip of no 2 words
        if(wordsArray.length<2)
        	return;
        
        for(int i=0; i<(wordsArray.length-1); i++){
        	String lastWord = wordsArray[i + 1].replaceAll("[^a-zA-Z]", "").toLowerCase();;
            String lastlastWord = wordsArray[i].replaceAll("[^a-zA-Z]", "").toLowerCase();;
	        if(lastWord.equals(lastlastWord)){
	            String wordToFind = wordsArray[i] + " " +  wordsArray[i + 1];
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
    
    private class ReplaceTask implements Runnable{
    	private final String completion;
    	private final int position, end;
    	
    	public ReplaceTask(String completion, int position, int end){
            this.completion = completion;
            this.position = position;
            this.end = end;
        }
		@Override
		public void run() {
			// TODO Auto-generated method stub
			textArea.replaceRange("", position, end);
			textArea.insert(completion, position);
            //textArea.setCaretPosition(end+1);
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
