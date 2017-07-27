package simplejavatexteditor;

import java.awt.Point;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;


public class CaretTools implements CaretListener {
	private final UI ui;
	private final JTextArea textArea;
	private Point cPos = new Point(0,0);
	
	public CaretTools(UI ui){
		this.ui = ui;
		textArea = ui.getEditor();
	}

    @Override
    public void caretUpdate(CaretEvent e) {
    	SwingUtilities.invokeLater(new PosChangeTask());
    }
    

    
    private class PosChangeTask implements Runnable{
    	@Override
        public void run() {
	    	int offset = 0;
	    	int caretPos = textArea.getCaretPosition();
	    	int rowNum = (caretPos == 0) ? 1 : 0;
	    	for (offset = caretPos; offset > 0;) {
	    	    try {
					offset = Utilities.getRowStart(textArea, offset) - 1;
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	    rowNum++;
	    	}
	    	
			try {
				offset = Utilities.getRowStart(textArea, caretPos);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	int colNum = caretPos - offset + 1;
	    	
	    	cPos = new Point(colNum, rowNum);
	    	
	    	//update UI
	    	ui.setCaretStatusLabel(cPos);
	    	ui.setPercentageStatusLabel((int)(100*cPos.y/(1.0*textArea.getLineCount())));
	    }
    }
}
