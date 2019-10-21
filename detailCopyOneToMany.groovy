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


def copyNodeDetails () {
    selectedNodes = c.selecteds
    sourceNode = selectedNodes[0]
    targetNodes = []
    
    if (selectedNodes.size()<2) {
        ui.informationMessage("Please select at least 2 nodes") 
        throw new Exception("At least 2 nodes")
    }
    
    for (i=1; i<selectedNodes.size(); i++) {
        targetNodes << selectedNodes[i]    
    }    
   
    JFrame frame = new JFrame("DetailsCopy");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    
    Object[] detailsOptions = ["Replace target details", "Append target details", "Cancel"];  
    int detailsAction = JOptionPane.showOptionDialog(frame, "What to do with target(s)' details'?", "Details copy action",  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, detailsOptions, detailsOptions[0]);
    
    if (sourceNode.detailsText != null){
        parseSrc = Jsoup.parse(sourceNode.detailsText)
        srcBody = parseSrc.body()
    }
    
    switch (detailsAction) {
        case 0:        
            targetNodes.each{eachNode ->
                if (sourceNode.detailsText != null) {
                    eachNode.setDetailsText(sourceNode.detailsText)
                    c.statusInfo = "DONE!"
                } else {
                    eachNode.setDetailsText(null)
                    c.statusInfo = "Source node has no details"
                }
            }
            break
        case 1:
                if (sourceNode.detailsText != null) {                                    
                    targetNodes.each{eachNode ->               
                        Document parseTarget = Jsoup.parse(eachNode.detailsText)
                        Element targetBody = parseTarget.body()                                    
                        targetBody.append(srcBody.html())                        
                        eachNode.details = parseTarget.html()                        
                    } 
                    c.statusInfo = "DONE!"
                 } else {
                        c.statusInfo = "Source node has no details"
                 }
            break        
        case 2:
            break
    }
}


copyNodeDetails()
