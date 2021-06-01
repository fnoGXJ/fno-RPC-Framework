package com.fno.rpc.telecommunication.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelProvider {
    private Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress address) {
        String addressStr = address.toString();
        if (!channelMap.containsKey(addressStr)) {
            Channel channel = channelMap.get(addressStr);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(addressStr);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        channelMap.put(inetSocketAddress.toString(), channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        channelMap.remove(inetSocketAddress.toString());
    }
}
