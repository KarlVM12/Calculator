import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;

public class CalculatorGUI implements ActionListener
{
    private JPanel contentPane = new JPanel();
    private JTextField inputScreen = new JTextField("0", 20);
    private String currentQuery = "";
    
    private double operand1, operand2;
    private String operator;
    private int operatorIndex = -1;

    public CalculatorGUI()
    {
        contentPane.setLayout(new BorderLayout(0, 20));

        // pane for buttons
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new GridLayout(5, 4, 20, 20));
        buttonPane.setBackground(Color.WHITE);

        String[] buttonStrings = {"7","8","9","+","4","5","6","-","1","2","3","*",".","0","=","/","Del","Clr"};
        JButton[] buttons = new JButton[buttonStrings.length];

        for(int i = 0; i < buttonStrings.length; i++)
        {
            buttons[i] = new JButton(buttonStrings[i]);
            buttons[i].setActionCommand("click");
            buttons[i].addActionListener(this);
            buttonPane.add(buttons[i]);          
        }

        
        // pane for textfield
        JPanel textPane = new JPanel();
        textPane.setLayout(new BorderLayout());
        textPane.setBackground(Color.WHITE);
        
        // editing input screen
        inputScreen.setEditable(false);
        inputScreen.setHorizontalAlignment(JTextField.RIGHT);
        inputScreen.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        
        textPane.add(inputScreen, BorderLayout.CENTER);
        textPane.add(new JLabel("           "), BorderLayout.WEST);
        textPane.add(new JLabel("           "), BorderLayout.EAST);

        // adding subpanes to main pane
        contentPane.add(textPane, BorderLayout.NORTH);
        textPane.setPreferredSize(new Dimension(400, 40));

        contentPane.add(buttonPane, BorderLayout.CENTER);
        contentPane.add(new JLabel("           "), BorderLayout.WEST);
        contentPane.add(new JLabel("           "), BorderLayout.EAST);

    } // end constructor

    public void actionPerformed(ActionEvent e)
    {
        if("click".equals(e.getActionCommand()))
        {   

            // gets current button press
            String currentClick = (((JButton)e.getSource()).getText());

            // Delete function
            if(currentClick.equals("Del"))
            {               
                // if on error or there is only one thing to delete 
                if(inputScreen.getText().equals("ERROR") || currentQuery.length() == 1)
                {
                    currentQuery = "";
                    inputScreen.setText("0");
                    return;
                }

                // if there is more than one thing to delete, deletes starting on the right
                if(currentQuery.length() > 0)
                {
                    currentQuery = currentQuery.substring(0, currentQuery.length()-1);
                    inputScreen.setText(currentQuery);
                }

                return;
            }

            // Clear function
            if(currentClick.equals("Clr"))
            {
                currentQuery = "";
                inputScreen.setText("0");
                return;
            }

            // when you hit =
            if(currentClick.equals("="))
            {
                // attempts to solve current query
                try
                {
                    // if no operator, only first operand is present so don't need to calculate anything
                    if(operatorIndex == -1)
                    {
                        if(currentQuery.equals(""))
                            inputScreen.setText("0");
                        else
                            inputScreen.setText(currentQuery);

                        return;
                    }

                    // if first operand and operator exist with no second operand and hit =, can't evaluate so throw error
                    if(currentQuery.length()-1 == operatorIndex)
                    {
                        throw new Exception("No Second Operand");
                    }

                    // if the length of the query is longer than where the operator is and there is a second operator, we can get the second operand
                    if(currentQuery.length() > operatorIndex && operatorIndex != -1)
                    {
                        operand2 = Double.parseDouble(currentQuery.substring(operatorIndex+1, currentQuery.length()));
                    }

                    
                    // if we successfully got all parts of the equation, attempt to solve
                    double result = solveQuery();
                    
                    System.out.println(currentQuery);
                    //System.out.println(operand1 + " " + operator + " " + operand2);
                    
                    // checks if result is just an int
                    if(result % 1 == 0)
                        currentQuery = ("" + (int)result);
                    else
                        currentQuery = ("" + result);

                    inputScreen.setText(currentQuery);

                    // reset variables for query
                    operand1 = operand2 = 0.0;
                    operatorIndex = -1;
                    operator = "";

                }
                catch(Exception ex)
                {
                    // on error, makes screen say ERROR and clears query
                    System.out.println(ex.getMessage());
                    System.out.println("Couldn't Evaluate Expression");
                    inputScreen.setText("ERROR");
                    currentQuery = "";
                }

            }
            else // anything clicked other than Del, Clr, or =
            {
                // if we want to enter a negative number as the first number
                if(currentQuery == "" && currentClick == "-")
                {
                    currentQuery += currentClick;
                    inputScreen.setText(currentQuery);
                    return;
                }

                // if any operator, stores the operator and the number behind it
                if(currentClick == "+" || currentClick == "-" || currentClick == "*" || currentClick == "/")
                {   

                    // for when you try to do a chaining equation like 3+3+, calculate 3+3 first so screen says 6+
                    try
                    {
                        // gets second operand at the end to continue from 3+3+ --> 6+
                        operand2 = Double.parseDouble(currentQuery.substring(operatorIndex+1, currentQuery.length()));
                        
                        if(validQuery())
                        {
                            double result = solveQuery();
                            
                            // checks if result is just an int
                            if(result % 1 == 0)
                                currentQuery = ("" + (int)result);
                            else
                                currentQuery = ("" + result);
                            
                            inputScreen.setText(currentQuery);
                        }

                        // reset query values
                        operand1 = operand2 = 0.0;
                        operatorIndex = -1;
                        operator = "";
            
                    }
                    catch(Exception ex)
                    {
                        System.out.println("No Chaining Equation");
                    }

                    // for regular calculation with [+-*/]
                    try
                    {
                        // gets operator and first operand
                        operator = currentClick;
                        operatorIndex = currentQuery.length();
                        operand1 = Double.parseDouble(currentQuery.substring(0, currentQuery.length()));
                    }
                    catch(Exception ex)
                    {
                        System.out.println("Operand is invalid");
                        //System.out.println(currentQuery);
                        //System.out.println(operand1 + " " + operator + " " + operand2);
                        inputScreen.setText("ERROR");
                        currentQuery = "";
                        
                        operand1 = operand2 = 0.0;
                        operatorIndex = -1;
                        operator = "";
                
                        return;
                    }
                }
                
                // if the trys are successful or the click isn't an operator, adds click to the currentQuery
                currentQuery += currentClick;
                inputScreen.setText(currentQuery);
                
            }

        }
    
    } // end actionPerformed()

    // attempts to solve the query, if can't throws an exception
    public double solveQuery() throws Exception
    {
        double result = 0.0;
        switch (operator) {
            case "+":
                result = operand1 + operand2;
                break;
            case "-":
                result = operand1 - operand2;
                break;
            case "*":
                result = operand1 * operand2;
                break;
            case "/":
                result = operand1 / operand2;
                break;
            default:
                throw new Exception("No Operator");   
        }

        return result;
    
    } // end solveQuery()

    // checks if currentQuery is valid or not by seeing if an exception is thrown when calculating 
    public boolean validQuery()
    {
        try
        {
            double result = solveQuery();
            
            // only return true if no exception is thrown
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Invalid Query");
            return false;
        }

    } // end validQuery()

    public static void createAndShowGUI()
    {
        //Create and set up the window.
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        CalculatorGUI newContentPane = new CalculatorGUI();
        newContentPane.contentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane.contentPane);
        
        // background white
        newContentPane.contentPane.setBackground(Color.WHITE);
        
        //Display the window.
        frame.pack();
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    public static void main(String[] beans) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }

}