/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.abstracts.AbstractConnectionMessageable;
import server.conection.ClientManager;
import server.conection.ConnectionCoordinator;
import server.events.ClientConnectionEvent;
import server.events.ClientSendProtocolEvent;
import server.events.ClientServerConnectionMessageEvent;
import server.listeners.ClientConnectionListener;
import server.listeners.ClientSendProtocolListener;

/**
 *
 * @author Paul
 */
public class Server extends AbstractConnectionMessageable implements ClientConnectionListener, ClientSendProtocolListener {

    private ServerSocket serverSocket;
    private ConnectionCoordinator connectionCoordinator;
    private final ClientManager clientManager;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.clientManager = new ClientManager();
        this.clientManager.addConnectionObserver((ClientConnectionListener) this);
        this.messageListeners = new ArrayList<>();
    }

    @Override
    public void connect() throws IOException {
        System.out.println("Server initialized");
        serverSocket = new ServerSocket(port);
        connectionCoordinator = new ConnectionCoordinator(serverSocket);
        connectionCoordinator.addConnectionObserver(this);
        connectionCoordinator.init();
    }

    @Override
    public void disconnect() {
        try {
            System.out.println("Server closing");
            clientManager.close();
            connectionCoordinator.close();
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Can't close server", ex);
        }
    }

    @Override
    public void onClientConnect(Object dispatcher, ClientConnectionEvent evt) {
        Client client = (Client) evt.getSource();
        
        System.out.println("Client connected: " + client.toString());
        
        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(client.getName() + " conectado"));
        clientManager.addProtocolListener(this);
        clientManager.addNewClient(client);

    }

    @Override
    public void onClientDisconnect(Object dispatcher, ClientConnectionEvent evt) {
        Client disconnectedClient = (Client) evt.getSource();
        System.out.println("Client disconnected: " + disconnectedClient.getName());
        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(disconnectedClient.getName() + " desconectado"));
        clientManager.setTimerForClientDisconnected(disconnectedClient);
    }

    @Override
    public void onClientReconnect(Object dispatcher, ClientConnectionEvent evt) {
        Client reconnectedClient = (Client) evt.getSource();
        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(reconnectedClient.getName() + " reconectado"));
        System.out.println("Client reconnected: " + reconnectedClient.getName());

    }

    @Override
    public void onReceiveProtocol(ClientSendProtocolEvent evt) {
        //mensajes que me dijo el cliente 
        //analize the message
        notifyOnSimpleMessage(new ClientServerConnectionMessageEvent(evt.getSource()));
    }

}
