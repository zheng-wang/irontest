package io.irontest;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Sets;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Environment;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.IPAccessHandler;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

/**
 * The factory used by Iron Test to create Jetty server.
 * Created by Zheng on 18/03/2017.
 */
@JsonTypeName("ip-controlled")
public class IPControlledServerFactory extends DefaultServerFactory {
    private Map<String, String> ipAccess;
    private static final String IP_ACCESS_PARAMETER_NAME_WHITE_LIST = "whiteList";

    public Map<String, String> getIpAccess() {
        return ipAccess;
    }

    public void setIpAccess(Map<String, String> ipAccess) {
        this.ipAccess = ipAccess;
    }

    @Override
    public Server build(Environment environment) {
        String whiteListStr = ipAccess.get(IP_ACCESS_PARAMETER_NAME_WHITE_LIST);
        String[] whiteListEntries = whiteListStr.split(",");
        final Set<String> whiteList = Sets.newHashSet(StringUtils.stripAll(whiteListEntries));
        whiteList.remove("");

        Server server = super.build(environment);
        IPAccessHandler ipAccessHandler = new IPAccessHandler() { //  ugly workaround to support exact IPv6 match (no pattern), trying to protect Java 1.7 users for now.
            @Override
            protected boolean isAddrUriAllowed(String addr, String path){
                if (isIPv6Address(addr)) {
                    return whiteList.isEmpty() || whiteList.contains(addr);
                } else {                       //  it is IPv4 address
                    return super.isAddrUriAllowed(addr, path);
                }
            }
        };
        for (String entry: whiteList) {
            if (!isIPv6Address(entry)) {
                ipAccessHandler.addWhite(entry);         //  IPv6 addresses can not be added into IPAccessHandler.
            }
        }
        ipAccessHandler.setHandler(server.getHandler());
        server.setHandler(ipAccessHandler);

        return server;
    }

    private boolean isIPv6Address(String addr) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(addr);
        } catch (UnknownHostException e) {
            return false;
        }
        return address instanceof Inet6Address;
    }
}
