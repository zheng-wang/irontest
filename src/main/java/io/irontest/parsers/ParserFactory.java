package io.irontest.parsers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class ParserFactory {
    private static ParserFactory instance;

    private Map<String, IronTestParser> parsers = new HashMap<String, IronTestParser>();

    private ParserFactory() { }

    public static synchronized ParserFactory getInstance() {
        if ( instance == null ) {
            instance = new ParserFactory();
        }
        return instance;
    }

    public IronTestParser getParser(String parserName) {
        IronTestParser parser = null;
        if (parserName != null) {
            parser = parsers.get(parserName);
            if (parser == null) {
                try {
                    String classname = "io.irontest.parsers.";
                    if (parserName.equals("DBInterface")) {
                        classname = classname + "SPDDBParser";
                    } else {
                        classname = classname + parserName + "Parser";
                    }

                    Class parserClass = Class.forName(classname);
                    parser = (IronTestParser) parserClass.newInstance();
                    parsers.put(parserName, parser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return parser;
    }
}
