package au.com.billon.stt.parsers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class ParserFactory {
    private static ParserFactory instance;

    private Map<String, STTParser> parsers = new HashMap<String, STTParser>();

    private ParserFactory() { }

    public static synchronized ParserFactory getInstance() {
        if ( instance == null ) {
            instance = new ParserFactory();
        }
        return instance;
    }

    public STTParser getParser(String parserName) {
        STTParser parser = null;
        if (parserName != null) {
            parser = parsers.get(parserName);
            if (parser == null) {
                try {
                    String classname = "au.com.billon.stt.parsers.";
                    if (parserName.equals("DBInterface")) {
                        classname = classname + "SPDDBParser";
                    } else {
                        classname = classname + parserName + "Parser";
                    }

                    Class parserClass = Class.forName(classname);
                    parser = (STTParser) parserClass.newInstance();
                    parsers.put(parserName, parser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return parser;
    }
}
