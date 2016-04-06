package io.irontest.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class HandlerFactory {
    private static HandlerFactory instance;

    private Map<String, IronTestHandler> handlers = new HashMap<String, IronTestHandler>();

    private HandlerFactory() { }

    public static synchronized HandlerFactory getInstance() {
        if ( instance == null ) {
            instance = new HandlerFactory();
        }
        return instance;
    }

    public IronTestHandler getHandler(String handlerName) {
        IronTestHandler handler = null;
        if (handlerName != null) {
            handler = handlers.get(handlerName);
            if (handler == null) {
                try {
                    Class handlerClass = Class.forName("io.irontest.handlers." + handlerName);
                    handler = (IronTestHandler) handlerClass.newInstance();
                    handlers.put(handlerName, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return handler;
    }
}
