/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.events;

import java.util.EventObject;

/**
 *
 * @author Paul
 */
public class ClientConnectionEvent extends EventObject{
    
    public ClientConnectionEvent(Object source) {
        super(source);
    }
    
}
