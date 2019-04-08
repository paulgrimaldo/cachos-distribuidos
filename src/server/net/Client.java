/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import server.abstracts.AbstractConnectionMessageable;
import server.helpers.Helper;
import server.abstracts.AbstractSocketWriter;
import server.events.ClientServerConnectionMessageEvent;

/**
 *
 * @author Paul
 */
public class Client extends AbstractConnectionMessageable implements Serializable, AbstractSocketWriter {

    private transient Socket clientSocket;
    private String name;
    private final String id;
    private transient DataInputStream inputStream;
    private transient DataOutputStream outputStream;
    private transient ClientServerConnection clientServerConnectionThread;

    public String getName() {
        return name;
    }

    public Client(String ip, int port, String name, String id) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.id = id;
        this.messageListeners = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public void setSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void connect() throws IOException {
        System.out.println("Client " + name + " trying to connect");

        clientSocket = new Socket(ip, port);
        createStreams();

        writeMessage(Helper.serialize(this));
        clientServerConnectionThread = new ClientServerConnection(clientSocket);
        clientServerConnectionThread.start();

    }

    @Override
    public void disconnect() throws IOException {
        System.out.println("Client " + name + " trying to disconnect");

        if (clientSocket != null) {
            clientSocket.close();
        }
        if (clientServerConnectionThread != null) {
            clientServerConnectionThread.stop();
        }

    }

    @Override
    public String toString() {
        return "Client:{port:" + port + ", ip:" + ip + ", name:" + name + ",id:" + id + "}";
    }

    @Override
    public void createStreams() throws IOException {
        inputStream = new DataInputStream(clientSocket.getInputStream());
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void writeMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    @Override
    public String readMessage() throws IOException {
        return inputStream.readUTF();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    private class ClientServerConnection extends Thread {

        private Socket connectionSocket;

        public ClientServerConnection(Socket socket) {
            this.connectionSocket = socket;
        }

        @Override
        public void run() {
            boolean isOk = true;
            while (isOk) {
                try {
                    String serverMessage = readMessage();
                    System.out.println(serverMessage);
                    notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(serverMessage));
                } catch (IOException ex) {
                    ///Cliente perdio la conexion al servidor
                    isOk = false;
                    notifyOnSimpleMessage(new ClientServerConnectionMessageEvent("Se perdio la conexion al servidor"));
                    System.out.println("Client cannot get the server");
                    tryToReconnect();
                }
            }
        }

        private void tryToReconnect() {
            final TimerTask task;
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        connect();
                        cancel();
                        ClientServerConnection.this.stop();
                    } catch (IOException e) {
                        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent("No puedo llegar al servidor"));
                        System.out.println("Can't get the server again");
                    }
                }
            };

            Timer timer = new Timer("Timer");
            long delay = 10000L;
            long period = 5000L;
            timer.scheduleAtFixedRate(task, delay, period);

        }

    }

}
