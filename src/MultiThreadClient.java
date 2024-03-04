//********************************************************************
//
//  Author:        Winton Haisler
//
//  Project #:     Two
//
//  File Name:     ClientCode.java
//
//  Course:        COSC 4301 Modern Programming
//
//  Due Date:      2/19/2024
//
//  Instructor:    Prof. Fred Kumi
//
//  Chapter:       Oracle OpenJDK 21.0.1
//
//  Description:   client code to take in 3 space seperated integers
//                 passing them to a server and displaying the response.
//
//********************************************************************

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MultiThreadClient
{
    //***************************************************************
    //
    //  Method:       main
    //
    //  Description:  The main method of the project
    //
    //  Parameters:   String array
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public static void main(String[] args)
    {
        MultiThreadClient clientObj = new MultiThreadClient();
        clientObj.connectToServer();
    }

    //***************************************************************
    //
    //  Method:       connectToServer
    //
    //  Description:  control connection to server
    //
    //  Parameters:   None
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void connectToServer()
    {
        try {
            InetAddress address = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(address, 4301);

            readClient(socket);

            socket.close();
            System.out.print("Closing socket.");

        } catch (IOException e) {
            System.err.println("Error: Server not found.");
        }
    }

    //***************************************************************
    //
    //  Method:       readClient
    //
    //  Description:  control reading from client user
    //                throws IOException to be handled by connectToServer
    //
    //  Parameters:   Socket socket
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void readClient(Socket socket) throws IOException
    {
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput = "";
        System.out.print("Enter a number or \"Bye\" to quit: ");

        while ((userInput = stdIn.readLine()) != null && !userInput.equalsIgnoreCase("bye"))
        {
            writeClient(output, userInput);
            readServer(input);
            System.out.print("Enter a number or \"Bye\" to quit: ");
        }
    }

    //***************************************************************
    //
    //  Method:       readServer
    //
    //  Description:  control reading from server
    //
    //  Parameters:   Socket socket
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void readServer(BufferedReader input) throws IOException
    {
        StringBuilder lines= new StringBuilder();
        String line="";
        while((line = input.readLine()) != null && !line.isEmpty()) {
            lines.append(line).append('\n');
        }
        System.out.println("Server Response: \n" + lines);
    }

    //***************************************************************
    //
    //  Method:       writeClient
    //
    //  Description:  display message on the client as well as
    //                sending the message to the server
    //
    //  Parameters:   PrintWriter, String
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void writeClient(PrintWriter output, String message)
    {
        System.out.println("Sending: " + message);
        output.println(message);
    }
}
