/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.application;

import server.listeners.ClientSendProtocolListener;

/**
 *
 * @author Paul
 */
public class ProtocolProcessor {

    private Protocol mProtocol;
    private ClientSendProtocolListener mClientSendProtocolListener;
    
    public void newProtocol(String protocol){
       this.mProtocol.setContent(protocol);
    }
    
    public void addClientSendProtocolListener(ClientSendProtocolListener listener) {
        mClientSendProtocolListener = listener;
    }
    
    public void process(){
        //////////
        //mClientSendProtocolListener.onReceiveProtocol(evt);
    }
}
