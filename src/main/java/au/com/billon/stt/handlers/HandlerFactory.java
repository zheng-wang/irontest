package au.com.billon.stt.handlers;

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

    public STTHandler getHandler(String handlerName) {
        STTHandler handler = null;
        if (handlerName != null) {
            try {
                Class handlerClass = Class.forName("au.com.billon.stt.handlers." + handlerName);
                handler = (STTHandler) handlerClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return handler;
    }
}
