/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.imagine.trending.beans;


import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import static javax.json.stream.JsonParser.Event.START_OBJECT;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author user
 */
@ServerEndpoint("/chat/{n}")
public class Chat{
    
    private static final Set<Session> ss = new HashSet();
    private Trending t;
    private InitialContext ic;
    @OnOpen
    public void open(Session s,@PathParam("n")String n){  
        try {
            ic = new InitialContext();
            t = (Trending)ic.lookup("java:module/Trending");
            s.getUserProperties().put("name", n);
            ss.add(s);
        } catch (NamingException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @OnMessage
    public void onMessage(Session s,String message) {
        String msg="",sender="",receiver="",disc="";
        JsonParser p = Json.createParser(new StringReader(message));
        while(p.hasNext()){
            Event e = p.next();
            switch (e) {
                case START_OBJECT:
                case END_OBJECT:    
                    break;
                case KEY_NAME:
                    switch (p.getString()){
                        case "sender":
                            p.next();
                            sender = p.getString();
                        break;
                        case "msg":
                            p.next();
                            msg = p.getString();
                            break;
                        case "receiver":
                            p.next();
                            receiver = p.getString();
                        break;
                        case "disc":
                            p.next();
                            disc = p.getString();
                        break;
                    }                
            }
        }
        for(Session v:ss){ 
            if(v.isOpen()){
                String n = (String) v.getUserProperties().get("name");
                if(n.equalsIgnoreCase(receiver)){
                    v.getAsyncRemote().sendText(disc+":"+sender+":"+msg);
                    t.saveM(receiver, msg, sender,false);
                    break;
                }
            }
        }
    }
    
    public static void sendEdit(Set<String> s){
        for(Session v:ss){
            if(v.isOpen()){
                String st = (String)v.getUserProperties().get("name");
                if(s.contains(st)){
                    v.getAsyncRemote().sendText("$"+s+" has just been edited");
                }
            }
        }
    }
    @OnClose
    public void close(Session s) throws IOException{
        s.getUserProperties().clear();
        s.close();
        ss.remove(s);
    }
}
