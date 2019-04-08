/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.conection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.abstracts.AbstractConnectionListener;
import server.net.Client;
import server.events.ClientConnectionEvent;
import server.events.ClientSendProtocolEvent;
import server.listeners.ClientSendProtocolListener;

/**
 *
 * @author Paul
 */
public class ClientManager extends AbstractConnectionListener {

    private final ArrayList<ClientSendProtocolListener> protocolListeners;
    private final HashMap<Client, Thread> connectedClients;
    private final HashMap<Client, ClientDisconnectionTaskManager> disconnectedClients;

    public ClientManager() {
        protocolListeners = new ArrayList<>(10);
        connectionListeners = new ArrayList<>(10);
        disconnectedClients = new HashMap<>(10);
        connectedClients = new HashMap<>(10);
    }

    public synchronized void destroyClient(Client client) {
        connectedClients.remove(client).stop();
    }

    public void addNewClient(Client client) {
        if (clientExists(client)) {
            destroyClient(client);
            removeFromDisconnected(client);
            notifyOnClientReconnect(this, new ClientConnectionEvent(client));
        }

        Thread clientThread = new Thread() {
            @Override
            public void run() {
                boolean connectionOk = true;
                try {
                    client.createStreams();
                } catch (IOException ex) {
                    Logger.getLogger(ClientManager.class.getName()).log(Level.SEVERE, "Error creating streams", ex);
                }

                while (connectionOk) {
                    try {
                        String newMessage = client.getName() + ": " + client.readMessage();
                        notifyOnNewProtocol(new ClientSendProtocolEvent(newMessage));
                        System.out.println(newMessage);

                        client.writeMessage("Server : Ok Receive");
                    } catch (IOException ex) {
                        connectionOk = false;
                        notifyOnClientDisconnect(this, new ClientConnectionEvent(client));
                    } catch (Exception ex) {
                        Logger.getLogger(ClientManager.class.getName()).log(Level.WARNING, null, ex);
                    }
                }

            }
        };

        System.out.println(client.getName() + "'s thread is created and running");
        connectedClients.put(client, clientThread);
        clientThread.start();
    }

    public boolean clientExists(Client client) {

        return connectedClients.keySet().contains(client);
    }

    public void setTimerForClientDisconnected(Client client) {
        ClientDisconnectionTaskManager clientDisconnectionTaskManager = new ClientDisconnectionTaskManager();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Removing disonnected client " + client.getName());
                destroyClient(client);
                removeFromDisconnected(client);
            }
        };

        long delay = 10000;
        clientDisconnectionTaskManager.setTask(task);
        clientDisconnectionTaskManager.executeTask(delay);
        disconnectedClients.put(client, clientDisconnectionTaskManager);
    }

    public synchronized void removeFromDisconnected(Client client) {
        disconnectedClients.remove(client).cancel();
    }

    public void close() {
        connectedClients.entrySet().forEach((entry) -> {
            Client client = entry.getKey();
            Thread clientThread = entry.getValue();
            clientThread.stop();
            connectedClients.remove(client);
        });

        disconnectedClients.entrySet().forEach((entry) -> {
            Client client = entry.getKey();
            ClientDisconnectionTaskManager manager = entry.getValue();
            manager.cancel();
            disconnectedClients.remove(client);
        });

    }

    public void addProtocolListener(ClientSendProtocolListener listener) {
        protocolListeners.add(listener);
    }

    public void notifyOnNewProtocol(ClientSendProtocolEvent event) {
        for (int i = 0; i < protocolListeners.size(); i++) {
            protocolListeners.get(i).onReceiveProtocol(event);
        }
    }

}
