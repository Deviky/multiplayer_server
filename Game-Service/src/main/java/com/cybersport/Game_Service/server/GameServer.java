package com.cybersport.Game_Service.server;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

@Service
public class GameServer {
    private final Map<Long, Map<Long, DatagramChannel>> roomPlayerChannels = new HashMap<>();



}
