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

//Get the list of selected nodes
selectedNodes = c.selecteds

//Get the source node
sourceNode = selectedNodes[0]

targetNodes = []

def copyNodeNote () {
    //Alert when only 1 node is selected
    if (selectedNodes.size()<2) {
        ui.informationMessage("Please select at least 2 nodes") 
        throw new Exception("At least 2 nodes")
    }
    
    //Add the target nodes to receive the notes from source node
    for (i=1; i<selectedNodes.size(); i++) {
        targetNodes << selectedNodes[i]    
    }    
   
    //Creating a dialog for choosing to replace or append notes of target nodes
    JFrame frame = new JFrame("NoteCopy");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Object[] noteOptions = ["Replace target note", "Append target note", "Cancel"];  

    //Selected action will be 0 - Replace, 1 - Append, 2 - cancel
    int noteAction = JOptionPane.showOptionDialog(frame, "What to do with target(s)' note'?", "Note copy action",  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, noteOptions, noteOptions[0]);
    
    //Get the content inside <body> tag of the source node's note
    if (sourceNode.noteText != null){
        parseSrc = Jsoup.parse(sourceNode.noteText)
        srcBody = parseSrc.body()
    }
    
    switch (noteAction) {
        case 0:        
            //Loop through each target nodes
            targetNodes.each{eachNode ->
                //Check if source node contain any note
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
            //Check if source node contain any note
            if (sourceNode.noteText != null) {                                    
                targetNodes.each{ eachNode ->               
                    //Check if each target node already has note, if it does, parse the <body> of the target note
                    if (eachNode.noteText != null){
                        Document parseTarget = Jsoup.parse(eachNode.noteText)
                        Element targetBody = parseTarget.body()
                        targetBody.append(srcBody.html())
                        eachNode.noteText = parseTarget.html()
                    } else {
                        //if target node does not have any note, set its note to the note of source node
                        eachNode.setNoteText(sourceNode.noteText)
                    }
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
//This check is to ensure the code is only executed once, otherwise it will be executed on every selected nodes
if (node == sourceNode){
    copyNodeNote()
}
