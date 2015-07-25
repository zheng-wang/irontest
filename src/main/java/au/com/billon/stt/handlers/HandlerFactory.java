package au.com.billon.stt.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class HandlerFactory {
    private static HandlerFactory instance;

    private Map<String, STTHandler> handlers = new HashMap<String, STTHandler>();

    private HandlerFactory() { }

    public static synchronized HandlerFactory getInstance() {
        if ( instance == null ) {
            instance = new HandlerFactory();
        }
        return instance;
    }

    public STTHandler getHandler(String handlerName) {
        STTHandler handler = null;
        if (handlerName != null) {
            handler = handlers.get(handlerName);
            if (handler == null) {
                try {
                    Class handlerClass = Class.forName("au.com.billon.stt.handlers." + handlerName);
                    handler = (STTHandler) handlerClass.newInstance();
                    handlers.put(handlerName, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return handler;
    }
}
