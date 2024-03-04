//********************************************************************
//
//  Author:        Winton Haisler
//
//  Project #:     Two
//
//  File Name:     ServerCode.java
//
//  Course:        COSC 4301 Modern Programming
//
//  Due Date:      2/19/2024
//
//  Instructor:    Prof. Fred Kumi
//
//  Chapter:       Oracle OpenJDK 21.0.1
//
//  Description:   server code take in a string of 3 space seperated
//  			   integers from a client, verify the input, calculate
//  			   statistics, and pass the results back to the client
//
//********************************************************************

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {


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
        MultiThreadServer serverObj = new MultiThreadServer();
        serverObj.developerInfo();

        int coreCount = Runtime.getRuntime().availableProcessors();

        serverObj.runServer(coreCount);

    }

    //***************************************************************
    //
    //  Method:       runServer
    //
    //  Description:  control connection to client
    //
    //  Parameters:   None
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void runServer(int cores){
        int port = 4301;
        ServerSocket serverSocket = null;

        try {
            // Create TCP Server Socket
            serverSocket = new ServerSocket(port);
            System.out.println("[TCP Server says] TCP Server created on port " + port);
        }
        catch (IOException e)
        {
            System.out.println("[TCP Server says] Error: TCP Server cannot be created on port " + port);
            System.exit(1);
        }

        while (true)
        {
            Socket clientSocket = null;

            try {
                // Start listening to incoming client request (blocking function)
                System.out.println("\n[TCP Server says] Waiting for connection.....");
                clientSocket = serverSocket.accept();
            }
            catch (IOException e)
            {
                System.err.println("[TCP Server says] Error: Cannot accept client request.");
                System.exit(1);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(cores);

            try {
                connectClients(clientSocket, serverSocket, executorService);
            } catch (IOException e) {
                System.err.println("[TCP Server says] Error: Cannot accept client request.");
            }
        }
    }

    private void connectClients(Socket clientSocket, ServerSocket serverSocket, ExecutorService executorService) throws IOException {

        try {
            // Create a new thread for each incoming message
            executorService.execute(new ProcessEachClient(clientSocket));
        }
        catch (Exception e)
        {
            System.err.println("[TCP Server says] Error: Cannot accept client request.");
            serverSocket.close();
            System.exit(1);
        }
    }


    //***************************************************************
    //
    //  Method:       developerInfo (Non Static)
    //
    //  Description:  The developer information method of the program
    //
    //  Parameters:   None
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void developerInfo()
    {
        System.out.println("Name:    Winton Haisler");
        System.out.println("Course:  COSC 4301 Modern Programming");
        System.out.println("Program: Tree\n\n");

    } // End of the developerInfo method
}
