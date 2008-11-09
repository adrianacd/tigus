package org.tigus.app.editor;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import org.tigus.core.*;

import java.util.*;

/**
 * Represents a panel used for showing the reviews of a question
 * @author Adriana Draghici
 *
 */
public class ReviewsTab extends JPanel{    
        
    private static final long serialVersionUID = 1L;
    
    JTabbedPane tabbedPane;
    JSplitPane splitPane;
    JScrollPane listScrollPane;
    
    JList reviewsList;
    DefaultListModel listModel;
    
    Vector <Review> reviews = new Vector<Review>();
    int index = -1; // index in the JList object
    int tabIndex;   // index of this panel in JTabbedPane object
    /**
     * Constructor
     * @param question the question
     */
    ReviewsTab(Question question, JTabbedPane tabbedPane){   
        
        reviews = (Vector <Review>)question.getReviews();
        this.tabbedPane = tabbedPane;
        tabIndex = tabbedPane.getTabCount();
        initComponents();
        
    }
    
    
    
    private void initComponents(){
      
        /* create list of reviews identified by their dates */
        
        listModel = new DefaultListModel(); 
        for (Review r : reviews) {
            listModel.addElement("<html><ul><li type=square>" + r.getDate() + "</ul></html>");
        }
        
        reviewsList = new JList();
        reviewsList.setModel(listModel);
        
        JScrollPane listScrollPane = new JScrollPane(reviewsList);          
        listScrollPane.setMinimumSize(new Dimension(100, 50));
    
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listScrollPane,
                                                        new JPanel());
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);
      
        JButton closeButton = new JButton("Close");
        
        /* add listeners */
        
        closeButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.remove(tabIndex);
           }
        });
        
        addListeners(); 
        
        
        add(splitPane);
        add(closeButton);
        
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));   
        setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
    }
    
    private void addListeners() {
        reviewsList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int newindex = reviewsList.getSelectedIndex();
                if(index == newindex)
                    return;
                index = newindex;
                showReviewPanel(reviews.elementAt(index));               
            }
        });
    }
    
    public void showReviewPanel(Review review) {
        splitPane.setRightComponent(new ReviewPanel(review));
    }
   
  
}

/**
 * Represents a panel in which a review is displayed
 * @author Adriana Draghici
 */
class ReviewPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    Review review;
    Change change;
    ReviewPanel(Review review) {
        super();
        this.review = review;
        this.change = review.getChange();
        
        initComponents();
    }
    private void initComponents() {
        JLabel dateLabel = new JLabel("<html><DL><DT>Date:<br><DD>" 
                                        + review.getDate()+"</DL></html>");
        JLabel authorLabel = new JLabel("<html><DL><DT>Author:<br><DD>" 
                                        + review.getAuthor()+"</DL></html>");
        JLabel commentLabel = new JLabel("<html><DL><DT>Comment:<br><DD>" 
                                        + review.getComment()+"</DL></html>");
       
        JPanel beforePanel = new JPanel();
        JPanel afterPanel = new JPanel();
        
        Question before = change.getPreviousQuestion();
        Question after = change.getQuestion();
        Boolean empty = true; //  true if no changes were made
        
        
        add(dateLabel);
        add(authorLabel);
        add(commentLabel);
 
        if(before.getText().equals(after.getText())) {
            add(new JLabel("<html><DL><DT> Question's Text: <br><DD>"+
                        before.getText()+"</DL><br></html>"));
           
        } 
        else { // if the text was modified
            empty = false;
            beforePanel.add(new JLabel("Question's Text:"));                   
            beforePanel.add(new JLabel(before.getText()));
            afterPanel.add(new JLabel("Question's Text:"));                   
            afterPanel.add(new JLabel(after.getText()));
        }
        
        // if the answers were modified
        if(!(before.getAnswers().isEmpty() && after.getAnswers().isEmpty())) { 
            empty = false;
            beforePanel.add(new JLabel("Answers:"));                   
            beforePanel.add(new JLabel(getAnswers(before)));
            afterPanel.add(new JLabel("Answers:"));                   
            afterPanel.add(new JLabel(getAnswers(after)));            
        }
        // if the tags were modified
        if(!(before.getTags().isEmpty() && after.getTags().isEmpty())) {
            empty = false;
            beforePanel.add(new JLabel("Tags:"));                   
            beforePanel.add(new JLabel(getTags(before)));
            afterPanel.add(new JLabel("Tags:"));                   
            afterPanel.add(new JLabel(getTags(after)));
        }        

        beforePanel.setLayout(new BoxLayout(beforePanel, BoxLayout.Y_AXIS)); 
        afterPanel.setLayout(new BoxLayout(afterPanel, BoxLayout.Y_AXIS));   
     
        beforePanel.setBorder(BorderFactory.createTitledBorder("Before"));
        afterPanel.setBorder(BorderFactory.createTitledBorder("After"));
        
        
        if(empty) {
            add(new JLabel("No changes"));
        }
        
        else {
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                    new JScrollPane(beforePanel),
                                    new JScrollPane(afterPanel));
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerLocation(150);
            add(splitPane);
            
        }
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));   
        setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    }
    
    /**
     * @param question
     * @return String object representing the question's answers
     */
    private String getAnswers(Question question) {
        String s = new String("<html> <ul>");
       
        Vector <Answer> answers = (Vector <Answer>)question.getAnswers();
        for(int i = 0; i < answers.size(); i++){
            if (answers.elementAt(i).isCorrect() == true){
                s += "<li type=circle>";
            }
            else s += "<li type=disc> ";
            s += answers.elementAt(i).getText();
        }  
        s += "</ul> </html>";
        return s;      
    }
    
    /**
     * @param question
     * @return String object representing the question's tags
     */
    private String getTags(Question question) {
        /* get tags */
        TagSet tagSet = question.getTags();
        Set <String> keys = tagSet.keySet();
        
        // the string contains a line for each tag, with "tagName: values"
        String text = new String("<html><DL><DT> Tags: <br>");       
        
        for (Iterator <String> it = keys.iterator(); it.hasNext(); ) {           
           String tagName = new String(it.next());           
          
           text += "<DD>";
           text += tagName;
           text += ": ";
           Vector <String> values = new Vector<String>(tagSet.get(tagName));
           Boolean first  = true; // used for identifying first value 
           for (String val : values){
               if(!first) 
                   text += ", ";             
               else
                   first = false;
               
               text += val; 
           }
           text += "<br>";          
        } 
        text += "</DL> </html>";
        
        return text;            
    }
    




}
