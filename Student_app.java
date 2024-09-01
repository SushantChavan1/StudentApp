
/*This project contain the details implementaitions ResultSet Interface and the Scrollable 
 * ResultSet so It contain the operations like INSERT UPDATE DELETE FIRS NEXT PREVIOUS ABSOLUTE
 * ect. copyrights@Sushan_Chavan
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Student_app {
    // Creating the reference
    public Connection con;
    public ResultSet rst;
    public Statement smt;
    public JFrame frame;
    public JLabel rollNumberLabel, nameLabel, marksLabel;
    public JTextField rollNumberField, nameField, marksField;
    public JButton insertButton, updateButton, deleteButton, displayButton;
    public JButton firstButton, lastButton, previousButton, nextButton, absoluteButton;

    public Student_app() {
        frame = new JFrame("Student Management System");
        frame.setLayout(null); // Use absolute positioning
        frame.setSize(450, 450); // Increased the frame size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and position labels
        rollNumberLabel = new JLabel("Roll Number:");
        rollNumberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        rollNumberLabel.setBounds(20, 20, 120, 30);
        frame.add(rollNumberLabel);

        nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setBounds(20, 70, 120, 30);
        frame.add(nameLabel);

        marksLabel = new JLabel("Marks:");
        marksLabel.setFont(new Font("Arial", Font.BOLD, 18));
        marksLabel.setBounds(20, 120, 120, 30);
        frame.add(marksLabel);

        // Create and position text fields with increased size
        rollNumberField = new JTextField();
        rollNumberField.setBounds(150, 20, 200, 30);
        frame.add(rollNumberField);

        nameField = new JTextField();
        nameField.setBounds(150, 70, 200, 30);
        frame.add(nameField);

        marksField = new JTextField();
        marksField.setBounds(150, 120, 200, 30);
        frame.add(marksField);

        // Create and position buttons with increased size and custom look
        insertButton = createStyledButton("Insert");
        insertButton.setBounds(20, 170, 120, 40);
        frame.add(insertButton);

        updateButton = createStyledButton("Update");
        updateButton.setBounds(150, 170, 120, 40);
        frame.add(updateButton);

        deleteButton = createStyledButton("Delete");
        deleteButton.setBounds(280, 170, 120, 40);
        frame.add(deleteButton);

        displayButton = createStyledButton("New Tab");
        displayButton.setBounds(20, 230, 120, 40);
        frame.add(displayButton);

        firstButton = createStyledButton("First");
        firstButton.setBounds(150, 230, 120, 40);
        frame.add(firstButton);

        lastButton = createStyledButton("Last");
        lastButton.setBounds(280, 230, 120, 40);
        frame.add(lastButton);

        previousButton = createStyledButton("Previous");
        previousButton.setBounds(20, 290, 120, 40);
        frame.add(previousButton);

        nextButton = createStyledButton("Next");
        nextButton.setBounds(150, 290, 120, 40);
        frame.add(nextButton);

        absoluteButton = createStyledButton("Absolute");
        absoluteButton.setBounds(280, 290, 120, 40);
        frame.add(absoluteButton);
        // Make frame visible
        frame.setVisible(true);
        // Adding ActionListeners to buttons
        insertButton.addActionListener(e -> insertValue());
        nextButton.addActionListener(e -> nextRecord());
        previousButton.addActionListener(e -> previousRecord());
        firstButton.addActionListener(e -> firstRecord());
        lastButton.addActionListener(e -> lastRecord());
        absoluteButton.addActionListener(e -> absoluteRecord());
        displayButton.addActionListener(e -> newThread());
        deleteButton.addActionListener(e -> deleteRecord());
        updateButton.addActionListener(e -> updateRecord());
        // Make connection with Oracle database
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "password");
            smt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rst = smt.executeQuery("SELECT roll_no, name, marks FROM student");
            if (rst.first()) {
                rollNumberField.setText(Integer.toString(rst.getInt("roll_no")));
                nameField.setText(rst.getString("name"));
                marksField.setText(Float.toString(rst.getFloat("marks")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Setting th font and color property to the buttons
    JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 153, 76));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        return button;
    }

    // This method perform to refresh the resultSet
    void refreshResultSet() {
        try {
            if (rst != null) {
                rst.close();
            }
            rst = smt.executeQuery("SELECT roll_no, name, marks FROM student");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This is used to update the record
    void updateRecord() {
        try {
            int rollNumber = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Roll Number to Update:"));
            String newName = JOptionPane.showInputDialog(frame, "Enter New Name:");
            float newMarks = Float.parseFloat(JOptionPane.showInputDialog(frame, "Enter New Marks:"));

            int rowcnt = -1;
            while (rst.next()) {
                if (rst.getInt("roll_no") == rollNumber) {
                    rowcnt = rst.getRow();
                    break;
                }
            }

            if (rowcnt != -1) {
                rst.absolute(rowcnt);
                rst.updateString("name", newName);
                rst.updateFloat("marks", newMarks);
                rst.updateRow();
                con.commit(); // Commit the transaction
                JOptionPane.showMessageDialog(frame, "Record updated successfully.");
                refreshResultSet(); // Refresh the ResultSet after the update
            } else {
                JOptionPane.showMessageDialog(frame, "Record with Roll Number " + rollNumber + " not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating record: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame,
                    "Invalid input. Please enter valid numbers for Roll Number and Marks.");
        }
    }

    // This method can delete the record
    void deleteRecord() {
        try {
            rst.first();
            int n = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Roll Number:"));
            int rowcnt = -1;
            while (rst.next()) {
                if (rst.getInt("roll_no") == n) {
                    rowcnt = rst.getRow();
                    break;
                }
            }
            if (rowcnt != -1) {
                rst.deleteRow();
                refreshResultSet();
                JOptionPane.showMessageDialog(frame, "Record deleted");
            } else {
                JOptionPane.showMessageDialog(frame, "Record is not found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error retrieving record: " + e.getMessage());
        }
    }

    // This method insert the values in database
    void insertValue() {
        try {
            int roll_n = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Roll Number:"));
            String name = JOptionPane.showInputDialog(frame, "Enter Name:");
            ;
            float mark = Float.parseFloat(JOptionPane.showInputDialog(frame, "Enter Marks:"));

            rst.moveToInsertRow();
            rst.updateInt("roll_no", roll_n);
            rst.updateString("name", name);
            rst.updateFloat("marks", mark);
            rst.insertRow();

            con.commit(); // Commit the transaction
            refreshResultSet();// After the insertion I am gona update the ResultSet
            JOptionPane.showMessageDialog(frame, "Record is inserted..");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Record is not inserted..");

        }
    }

    // Here I try to make the another instance of class(Thread)
    void newThread() {
        new Student_app();
    }

    // This method is search the record according to the roll_no and display the
    // data
    void absoluteRecord() {
        try {
            // Move to the specified row
            rst.first();
            int n = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Roll Number:"));
            int rowcnt = -1;
            while (rst.next()) {
                if (rst.getInt("roll_no") == n) {
                    rowcnt = rst.getRow();
                    break;
                }
            }
            // Display the data
            if (rowcnt != -1) {
                rst.absolute(rowcnt);
                rollNumberField.setText(Integer.toString(rst.getInt("roll_no")));
                nameField.setText(rst.getString("name"));
                marksField.setText(Float.toString(rst.getFloat("marks")));
            } else {
                // Here I am just using the Dilog box to show erroe msg
                JOptionPane.showMessageDialog(frame, "Record Not Found !");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error retrieving record: " + e.getMessage());
        }
    }

    // This method displays the next record in ResultSet
    void nextRecord() {
        try {
            if (rst != null && rst.next()) {
                rollNumberField.setText(Integer.toString(rst.getInt("roll_no")));
                nameField.setText(rst.getString("name"));
                marksField.setText(Float.toString(rst.getFloat("marks")));
            } else {
                JOptionPane.showMessageDialog(frame, "No more records.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // This is used to display the previous record in ResultSet
    void previousRecord() {
        try {
            if (rst != null && rst.previous()) {
                rollNumberField.setText(Integer.toString(rst.getInt("roll_no")));
                nameField.setText(rst.getString("name"));
                marksField.setText(Float.toString(rst.getFloat("marks")));
            } else {
                JOptionPane.showMessageDialog(frame, "No previous records.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // This is used to display first record in ResultSet
    void firstRecord() {
        try {
            if (rst != null && rst.first()) {
                rollNumberField.setText(Integer.toString(rst.getInt("roll_no")));
                nameField.setText(rst.getString("name"));
                marksField.setText(Float.toString(rst.getFloat("marks")));
            } else {
                JOptionPane.showMessageDialog(frame, "No records available.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // This is used to display the last record from ResutlSet
    void lastRecord() {
        try {
            if (rst != null && rst.last()) {
                rollNumberField.setText(Integer.toString(rst.getInt("roll_no")));
                nameField.setText(rst.getString("name"));
                marksField.setText(Float.toString(rst.getFloat("marks")));
            } else {
                JOptionPane.showMessageDialog(frame, "No records available.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Main function
    public static void main(String[] args) throws Exception {
        new Student_app();
    }
}
