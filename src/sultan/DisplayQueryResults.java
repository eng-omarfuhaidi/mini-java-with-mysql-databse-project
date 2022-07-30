
package sultan;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;

/**
 *
 * @author Sultan
 */
public class DisplayQueryResults  extends JFrame{

    /**
     * @param args the command line arguments
     */
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DATABASE_URL="jdbc:mysql://localhost/books";
    static final String USERNAME="root";
    static final String PASSWORD="";
    static final String DEFAULT_QUERY="Select * FROM Authors";

    //static final String DEFAULT_QUERY="SELECT FirstName,LastName,Title,Edition FROM Authors INNER JOIN AuthorISBN ON Authors.AuthorID=AuthorISBN.AuthorID INNER JOIN Titles ON AuthorISBN.ISBN=Titles.ISBN";
    
    private ResultSetTableModel tableModel;
   private JTextArea queryArea;
     
    // create ResultSetTableModel and GUI
     public DisplayQueryResults() 
     {   
        super( "Displaying Query Results" );
         
        // create ResultSetTableModel and display database table
        try 
       {
           // create TableModel for results of query SELECT * FROM authors
            tableModel = new ResultSetTableModel( JDBC_DRIVER, DATABASE_URL, 
              USERNAME, PASSWORD, DEFAULT_QUERY );
          
           // set up JTextArea in which user types queries
           queryArea = new JTextArea( DEFAULT_QUERY, 3, 100 );
           queryArea.setWrapStyleWord( true );
          queryArea.setLineWrap( true );
         
          JScrollPane scrollPane = new JScrollPane( queryArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
             ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
         
           // set up JButton for submitting queries
          JButton submitButton = new JButton( "Submit Query" );
 
          // create Box to manage placement of queryArea and 
          // submitButton in GUI
          Box box = Box.createHorizontalBox();
          box.add( scrollPane );
           box.add( submitButton );
 
         // create JTable delegate for tableModel 
           JTable resultTable = new JTable( tableModel );
          
          // place GUI components on content pane
         add( box, BorderLayout.NORTH );
           add( new JScrollPane( resultTable ), BorderLayout.CENTER );
 
          // create event listener for submitButton
          submitButton.addActionListener( 
          
            new ActionListener() 
            {
                 // pass query to table model
                public void actionPerformed( ActionEvent event )
              {
                    // perform a new query
                   try 
                   {
                      tableModel.setQuery( queryArea.getText() );
                  } // end try
                   catch ( SQLException sqlException ) 
                   {
                     JOptionPane.showMessageDialog( null, 
                       sqlException.getMessage(), "Database error", 
                         JOptionPane.ERROR_MESSAGE );
                     
                      // try to recover from invalid user query 
                     // by executing default query
                      try 
                     {
                        tableModel.setQuery( DEFAULT_QUERY );
                         queryArea.setText( DEFAULT_QUERY );
                       } // end try
                     catch ( SQLException sqlException2 ) 
                      {
                         JOptionPane.showMessageDialog( null, 
                            sqlException2.getMessage(), "Database error", 
                           JOptionPane.ERROR_MESSAGE );
          
                        // ensure database connection is closed
                         tableModel.disconnectFromDatabase();
          
                         System.exit( 1 ); // terminate application
                     } // end inner catch                   
                  } // end outer catch
                } // end actionPerformed
             }  // end ActionListener inner class          
          ); // end call to addActionListener

         setSize( 500, 250 ); // set window size
          setVisible( true ); // display window  
       } // end try
       catch ( ClassNotFoundException classNotFound ) 
      {
          JOptionPane.showMessageDialog( null, 
             "MySQL driver not found", "Driver not found",
             JOptionPane.ERROR_MESSAGE );
          
          System.exit( 1 ); // terminate application
      } // end catch
      catch ( SQLException sqlException ) 
       {
          JOptionPane.showMessageDialog( null, sqlException.getMessage(), 
             "Database error", JOptionPane.ERROR_MESSAGE );
                
         // ensure database connection is closed
          tableModel.disconnectFromDatabase();
          
          System.exit( 1 );   // terminate application
       } // end catch
       
       // dispose of window when user quits application (this overrides
      // the default of HIDE_ON_CLOSE)
      setDefaultCloseOperation( DISPOSE_ON_CLOSE );
      
       // ensure database connection is closed when user quits application
       addWindowListener(
      
         new WindowAdapter() 
         {
             // disconnect from database and exit when window has closed
             public void windowClosed( WindowEvent event )
            {
               tableModel.disconnectFromDatabase();
              System.exit( 0 );
             } // end method windowClosed
          } // end WindowAdapter inner class
       ); // end call to addWindowListener
    } // end DisplayQueryResults constructor
   
    
    
    public static void main(String[] args) {
        
        new DisplayQueryResults();  
    }
    
}
