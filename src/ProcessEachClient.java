//********************************************************************
//
//  Author:        Winton Haisler
//
//  Project #:     Three
//
//  File Name:     ProcessEachClient.java
//
//  Course:        COSC 4301 Modern Programming
//
//  Due Date:      3/9/2024
//
//  Instructor:    Prof. Fred Kumi
//
//  Chapter:       Oracle OpenJDK 21.0.1
//
//  Description:   processing thread extending Runnable to handle
//                 reading and validating a range of 2 integers,
//                 finding the prime numbers in the range and returning
//                 the list of primes, sum, mean and standard deviation
//                 of the primes.
//
//********************************************************************


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class ProcessEachClient implements Runnable{
    protected Socket clientSocket = null;
    private final String splitRegex = "(\s*,\s*)|(\s+)";

    public ProcessEachClient(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    //***************************************************************
    //
    //  Method:       run
    //
    //  Description:  The overriden method of Runnable
    //
    //  Parameters:   None
    //
    //  Returns:      N/A
    //
    //**************************************************************
    @Override
    public void run() {
        try {
            talkClient();
        } catch (IOException e) {
            System.err.println("Error: Client disconnected unexpectedly.");
        }
    }

    //***************************************************************
    //
    //  Method:       talkClient
    //
    //  Description:  manage communication between client and server
    //  			  throws IOException to be handled by run
    //
    //  Parameters:   Socket socket
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void talkClient() throws IOException {

        PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connection accepted on thread" + Thread.currentThread().getName());
        String in = "";

        while ((in = input.readLine()) != null && !in.equalsIgnoreCase("bye"))
        {
            System.out.println("Received: " + in);
            write(output, calcStats(in));
        }
        write(output, "Bye");
    }

    //***************************************************************
    //
    //  Method:       write
    //
    //  Description:  display message on the server as well as
    //                sending the message to the client
    //
    //  Parameters:   PrintWriter, String
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void write(PrintWriter output, String message)
    {
        System.out.println("Server Sending: \n" + message);
        output.println(message);
        output.println("");
    }

    //***************************************************************
    //
    //  Method:       calcStats
    //
    //  Description:  if input is valid calculate and return statistics
    //  			  otherwise return error message
    //
    //  Parameters:   String input
    //
    //  Returns:      String message
    //
    //**************************************************************
    public String calcStats(String input)
    {
        int sum = 0;
        double mean = 0.0;
        double stdiv = 0.0;
        String strValue = validateInput(input);

        if(strValue.isEmpty()) // all input valid
        {
            int[] args = Arrays.stream(input.split(splitRegex))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            ArrayList<Integer> data = createDataList(args);

            sum = data.stream().reduce(0, Integer::sum);
            mean = (sum*1.0)/data.size();

            for(Integer val : data)
            {
                stdiv += Math.pow(val - mean, 2);
            }
            stdiv = Math.sqrt(stdiv/(data.size()-1));

            strValue = String.format("Thread ID: %s%nPrime numbers: %s%nSum: %d%nMean: %,.2f%nStandard Deviation: %,.2f",
                    Thread.currentThread().getName(),data,sum, mean, stdiv);
        }

        return strValue;
    }

    //***************************************************************
    //
    //  Method:       isPrime
    //
    //  Description:  return boolean true if number is prime otherwise false
    //
    //  Parameters:   int
    //
    //  Returns:      boolean
    //
    //**************************************************************
    public boolean isPrime(int number)
    {
        boolean rtnValue = true;

        if (number < 2)            // Integers < 2 cannot be prime
            rtnValue = false;
        else if (number == 2)      // Special case: 2 is the only even prime number
            rtnValue = true;
        else if (number % 2 == 0)  // Other even numbers are not prime
            rtnValue = false;
        else {
            // Test odd divisors up to the square root of number
            // If any of them divide evenly into it, then number is not prime
            for (int divisor = 3; divisor <= Math.sqrt(number); divisor += 2)
            {
                if (number % divisor == 0)
                    rtnValue = false;
            }
        }

        return rtnValue;
    }

    //***************************************************************
    //
    //  Method:       createDataList
    //
    //  Description:  populate an ArrayList of prime integers
    //                between a range
    //
    //  Parameters:   int [2] array of input arguments [start, end]
    //
    //  Returns:      ArrayList<Integer>
    //
    //**************************************************************
    public ArrayList<Integer> createDataList(int[]args)
    {
        ArrayList<Integer> data = new ArrayList<>();
        for(int i = args[0]; i <= args[1]; i++)
        {
            if(isPrime(i))
                data.add(i);
        }
        return data;
    }
    //***************************************************************
    //
    //  Method:       validateInput
    //
    //  Description:  check if valid input String
    //  			     if it is valid return an empty string
    //  			     otherwise return an error message
    //
    //  Parameters:	  String input (should be 2 space and/or comma
    //                seperated integer values)
    //
    //  Returns:      String message
    //
    //**************************************************************
    public String validateInput(String input)
    {
        String out = "";
        String[] args = input.split(splitRegex);
        int[] intArgs = new int[2];

        if(args.length != 2)
        {
            out = "Invalid number of arguments. Enter 2 space or comma seperated positive integers.";
        }

        for(int i = 0; i < args.length && out.isEmpty(); i++)
        {
            String tmpOut = checkNum(args[i]);
            if (tmpOut.isEmpty()) // valid number
            {
                intArgs[i] = Integer.parseInt(args[i]);
            }
            out = tmpOut;
        }
        if(out.isEmpty()) // all numbers valid
        {
            if(intArgs[0] >= intArgs[1])
            {
                out = "Error: The 1st integer must be less than the 2nd.";
            }
        }
        return out;
    }

    //***************************************************************
    //
    //  Method:       checkNum
    //
    //  Description:  check if input String is a positive integer
    //  			  if it is a positive integer return an empty string
    //  			  otherwise return an error message
    //
    //  Parameters:	  String value
    //
    //  Returns:      String message
    //
    //**************************************************************
    public String checkNum(String value)
    {
        String out = "";
        try{
            int intValue = Integer.parseInt(value);
            if (intValue < 1)
            {
                out = value + " is an invalid integer. Enter positive integers only. ";
            }

        } catch (NumberFormatException exp) {
            out = value + " is an invalid integer. Enter positive integers only. ";
        }
        return out;
    }
}
