//********************************************************************
//
//  Author:        Winton Haisler
//
//  Project #:     Three
//
//  File Name:     MultiThreadServer.java
//
//  Course:        COSC 4301 Modern Programming
//
//  Due Date:      3/9/2024
//
//  Instructor:    Prof. Fred Kumi
//
//  Chapter:       Oracle OpenJDK 21.0.1
//
//  Description:   Server code to handle connection and assignment
//                 of clients to processing threads
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
    //  Description:  control connecting to clients
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
            serverSocket = new ServerSocket(port);
            System.out.println("Server created on port " + port);
        }
        catch (IOException e)
        {
            System.out.println("Error: Server cannot be created on port " + port);
            System.exit(1);
        }

        while (true)
        {
            Socket clientSocket = null;

            try {
                // Start listening to incoming client request (blocking function)
                System.out.println("\nWaiting for connection.....");
                clientSocket = serverSocket.accept();
            }
            catch (IOException e)
            {
                System.err.println("Error: Cannot accept client request.");
                System.exit(1);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(cores);

            try {
                connectClient(clientSocket, serverSocket, executorService);
            } catch (IOException e) {
                System.err.println("Error: Cannot accept client request.");
            }
        }
    }

    //***************************************************************
    //
    //  Method:       connectClient
    //
    //  Description:  connection to single client using separate
    //                thread via ExecutorService
    //
    //  Parameters:   Socket, ServerSocket, ExecutorService
    //
    //  Returns:      N/A
    //
    //**************************************************************
    private void connectClient(Socket clientSocket, ServerSocket serverSocket, ExecutorService executorService) throws IOException {

        try {
            // Create a new thread for each incoming message
            executorService.execute(new ProcessEachClient(clientSocket));
        }
        catch (Exception e)
        {
            System.err.println("Error: Cannot accept client request.");
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
        System.out.println("Program: Three\n\n");

    } // End of the developerInfo method
}
