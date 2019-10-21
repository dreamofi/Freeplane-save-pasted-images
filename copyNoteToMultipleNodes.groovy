import javax.swing.*;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import javax.swing.JFrame;

def selectedNodes = c.selecteds


if (selectedNodes.size()<2) {
    ui.informationMessage("Please select at least 2 nodes") 
    throw new Exception("At least 2 nodes")
}

def firstNode = selectedNodes[0]
def targetNode = []

for (i=1; i<selectedNodes.size(); i++) {
    targetNode << selectedNodes[i]    
}

println targetNode
JFrame frame = new JFrame("DialogDemo");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//int overWrite = JOptionPane.showConfirmDialog(frame, "Overwrite?", "Overwrite?", JOptionPane.YES_NO_OPTION);
//int overWrite = JOptionPane.showOptionDialog(frame, "Would you like to overwrite or merge notes of target nodes?", "Overwrite or merge notes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

Object[] options = ["Overwrite both", "Merge both", "Overwrite Notes only", "Merge Notes only", "Overwrite Attr only", "Merge Attr only", "Merge Notes, Overwrite Attr", "Merge Attr, Overwrite Note"];
               
Object selected = JOptionPane.showInputDialog(frame, "What to do with notes and attributes?", "Choose your action", JOptionPane.DEFAULT_OPTION, null, options, "Overwrite both");

if ( selected != null ){//null if the user cancels. 
    String selectedString = selected.toString();
    println selectedString
    //do something
}else{
    System.out.println("User cancelled");
}

def overwriteNote(first, targets){
    targets.each{eachNode ->
        eachNode.noteText = firstNode.noteText
    }
}

def overwriteAttr(first, targets){
    targets.each{eachNode ->
        eachNode.noteText = firstNode.noteText
    }
}
switch()
