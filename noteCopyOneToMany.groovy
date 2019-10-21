@Grab('org.jsoup:jsoup:1.12.1')
import javax.swing.*;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


def copyNodeNote () {
    def selectedNodes = c.selecteds
    def sourceNode = selectedNodes[0]
    def targetNodes = []
    
    if (selectedNodes.size()<2) {
        ui.informationMessage("Please select at least 2 nodes") 
        throw new Exception("At least 2 nodes")
    }
    
    for (i=1; i<selectedNodes.size(); i++) {
        targetNodes << selectedNodes[i]    
    }    
   
    JFrame frame = new JFrame("NoteCopy");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    
    Object[] noteOptions = ["Replace target note", "Append target note", "Cancel"];  
    int noteAction = JOptionPane.showOptionDialog(frame, "What to do with target(s)' note'?", "Note copy action",  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, noteOptions, noteOptions[0]);
    
    if (sourceNode.noteText != null){
        parseSrc = Jsoup.parse(sourceNode.noteText)
        srcBody = parseSrc.body()
    }
    
    switch (noteAction) {
        case 0:        
            targetNodes.each{eachNode ->
                if (sourceNode.noteText != null) {
                    eachNode.setNoteText(sourceNode.noteText)
                    c.statusInfo = "DONE!"
                } else {
                    eachNode.setNoteText(null)    
                    c.statusInfo = "Source node has no note"
                }
            }
            break
        case 1:
                if (sourceNode.noteText != null) {                                    
                    targetNodes.each{eachNode ->               
                        Document parseTarget = Jsoup.parse(eachNode.noteText)
                        Element targetBody = parseTarget.body()
                        targetBody.append(srcBody.html())
                        eachNode.noteText = parseTarget.html()
                    }
                    c.statusInfo = "DONE!" 
                 } else {
                 	c.statusInfo = "Source node has no note"                 	
                 }
            break        
        case 2:
            break
    }
}


copyNodeNote()
