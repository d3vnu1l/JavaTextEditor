/**
 * @name        Simple Java NotePad
 * @package     ph.notepad
 * @file        UI.java
 * @author      SORIA Pierre-Henry
 * @email       pierrehs@hotmail.com
 * @link        http://github.com/pH-7
 * @copyright   Copyright Pierre-Henry SORIA, All Rights Reserved.
 * @license     Apache (http://www.apache.org/licenses/LICENSE-2.0)
 * @create      2012-05-04
 * @update      2016-21-03
 *
 *
 * @modifiedby  Achintha Gunasekara
 * @modweb      http://www.achinthagunasekara.com
 * @modemail    contact@achinthagunasekara.com
 */

package simplejavatexteditor;

import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

public class SimpleJavaTextEditor extends JTextPane {

    private static final long serialVersionUID = 1L;
    public final static String ORIGINAL_AUTHOR_EMAIL = "pierrehenrysoria@gmail.com";
    public final static String ORIGINAL_EDITOR_EMAIL = "contact@achinthagunasekara.com";
    public final static String NAME = "PHNotePadIM";
    public final static String EDITOR_EMAIL = "radeushane@cpp.edu";
    public final static double VERSION = 3.01;

    /**
     * @param args
     */
    public static void main(String[] args) {
        new UI().setVisible(true);
        
    }

}