package au.com.billon.stt.handlers;

import au.com.billon.stt.models.Endpoint;

import java.lang.reflect.Constructor;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class HandlerFactory {
    private static HandlerFactory instance;

    private HandlerFactory() { }

    public static synchronized HandlerFactory getInstance() {
        if ( instance == null ) {
            instance = new HandlerFactory();
        }
        return instance;
    }

    public STTHandler getHandler(Endpoint endpoint) {
        String handlerName = endpoint.getHandler();
        STTHandler handler = null;
        if (handlerName != null) {
            try {
                Class handlerClass = Class.forName("au.com.billon.stt.handlers." + handlerName);
                Constructor handlerConstructor = handlerClass.getConstructor(Endpoint.class);
                handler = (STTHandler) handlerConstructor.newInstance(endpoint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return handler;
    }
}
