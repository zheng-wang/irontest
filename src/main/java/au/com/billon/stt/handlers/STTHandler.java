package au.com.billon.stt.handlers;

import com.fasterxml.jackson.core.JsonGenerationException;

import java.io.IOException;

/**
 * Created by Trevor Li on 7/14/15.
 */
public interface STTHandler {
    public String invoke(String request) throws Exception;
}
