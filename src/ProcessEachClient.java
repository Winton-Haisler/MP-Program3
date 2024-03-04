//********************************************************************
//
//  Author:        Instructor
//
//  Program #:     Three and Four
//
//  File Name:     ProcessEachClient.java
//
//  Course:        COSC 4301 Modern Programming
//
//  Instructor:    Prof. Fred Kumi
//
//  Description:   Thread Server example
//
//********************************************************************

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    //  			  throws IOException to be handled by runServer
    //
    //  Parameters:   Socket socket
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void talkClient() throws IOException {

        PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connection accepted.");
        String in = "";

        while ((in = input.readLine()) != null)
        {
            System.out.println("Received: " + in);
            if (in.equalsIgnoreCase("bye"))
            {
                write(output, "Bye");
                break;
            }
            else
            {
                write(output, calcStats(in));
            }
        }
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
            ArrayList<Integer> primeList = createPrimeList(data);

            sum = data.stream().reduce(0, Integer::sum);
            mean = (sum*1.0)/data.size();

            for(Integer val : data)
            {
                stdiv += Math.pow(val - mean, 2);
            }
            stdiv = Math.sqrt(stdiv/(data.size()-1));

            strValue = String.format("Thread ID: %s%nPrime numbers: %s%nSum: %d%nMean: %,.2f%nStandard Deviation: %,.2f",
                    Thread.currentThread().getName(),primeList,sum, mean, stdiv);
        }

        return strValue;
    }

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

    private ArrayList<Integer> createPrimeList(ArrayList<Integer> data)
    {
        return data.stream().filter(num -> isPrime(num)).collect(Collectors.toCollection(ArrayList::new));
    }

    //***************************************************************
    //
    //  Method:       createDataList
    //
    //  Description:  populate list of even/odd integers between a range
    //
    //  Parameters:   int [3] array of input arguments [start, end, even/odd]
    //
    //  Returns:      ArrayList<Integer>
    //
    //**************************************************************
    public ArrayList<Integer> createDataList(int[]args)
    {
        ArrayList<Integer> data = new ArrayList<>();
        for(int i = args[0]; i <= args[1]; i++)
        {
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
    //  Parameters:	  String input (should be 3 space seperated integer values)
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
                out = value + " is an invalid number. Enter positive integers only. ";
            }

        } catch (NumberFormatException exp) {
            out = value + " is not a number. Enter positive integers only. ";
        }
        return out;
    }
}