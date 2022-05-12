package ru.nsu.korovina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.korovina.models.UserAndKey;
import ru.nsu.korovina.services.parsers.JaxbOsmXmlParser;
import ru.nsu.korovina.services.parsers.OsmXmlParser;
import ru.nsu.korovina.services.writers.ConsoleWriter;
import ru.nsu.korovina.services.writers.Writer;
import ru.nsu.korovina.services.parsers.Parser;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        startApplication(args);
    }

    private static void startApplication(String[] args) {
        logger.info("Checking the number of input arguments...");

        if (args.length != 1) {
            throw new IllegalArgumentException("Error: the number of input arguments must be 1");
        }

        logger.info("The number of input arguments is 1 - OK");

        //для первой задачи
        Parser parser = new OsmXmlParser();
        //для второй задачи
        //Parser parser = new JaxbOsmXmlParser();
        Writer writer = new ConsoleWriter();

        UserAndKey result = parser.parse(args[0]);
        if (result != null) {
            writer.write(result);
        }
    }
}
