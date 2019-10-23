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
    
def copyNodeDetails () {
    //Alert when only 1 node is selected
    if (selectedNodes.size()<2) {
        ui.informationMessage("Please select at least 2 nodes") 
        throw new Exception("At least 2 nodes")
    }

    //Add the target nodes to receive the detail from source node
    for (i=1; i<selectedNodes.size(); i++) {
        targetNodes << selectedNodes[i]    
    }     
    
    //Creating a dialog for choosing to replace or append detail of target nodes
    JFrame frame = new JFrame("DetailsCopy");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    	
    
    Object[] detailsOptions = ["Replace target details", "Append target details", "Cancel"];  

    //Selected action will be 0 - Replace, 1 - Append, 2 - cancel
    int detailsAction = JOptionPane.showOptionDialog(frame, "What to do with target(s)' details'?", "Details copy action",  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, detailsOptions, detailsOptions[0]);

    //Get the content inside <body> tag of the source node's detail
    if (sourceNode.detailsText != null){
        parseSrc = Jsoup.parse(sourceNode.detailsText)
        srcBody = parseSrc.body()
    }
    
    switch (detailsAction) {
        case 0:        
            //Loop through each target nodes
            targetNodes.each{eachNode ->
                //Check if source node contain any detail
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
            //Check if source node contain any detail
            if (sourceNode.detailsText != null) {                                    
                targetNodes.each{ eachNode ->               
                    //Check if each target node already has detail, if it does, parse the <body> of the target detail
                    if (eachNode.detailsText != null){
                        Document parseTarget = Jsoup.parse(eachNode.detailsText)
                        Element targetBody = parseTarget.body()
                        targetBody.append(srcBody.html())                        
                        eachNode.details = parseTarget.html()        			
                    } else {
                        //if target node does not have any detail, set its detail to the detail of source node
                        eachNode.setDetailsText(sourceNode.detailsText)
                    }
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

//This check is to ensure the code is only executed once, otherwise it will be executed on every selected nodes
if (node == sourceNode) {
    copyNodeDetails()
}
