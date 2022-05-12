package ru.nsu.korovina.services.parsers;

import ru.nsu.korovina.models.UserAndKey;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OsmXmlParser implements Parser {
    private static final Logger logger = LoggerFactory.getLogger(OsmXmlParser.class);
    private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    private static final String TAG_NODE = "node";
    private static final String TAG_TAG = "tag";
    private static final String ATTR_USER = "user";
    private static final String ATTR_KEY = "k";

    @Override
    public UserAndKey parse(String resources) {
        UserAndKey result = null;

        try (InputStream bzIs = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(resources)))) {
            XMLEventReader reader = inputFactory.createXMLEventReader(bzIs);
            logger.info("Processing osm xml started");
            result = getStatisticForUsersAndKeys(reader);

        } catch (FileNotFoundException ex) {
            logger.error(String.format("Error: file %s was not found", resources), ex);
        } catch (IOException ex) {
            logger.error(String.format("Error: input exception with file: %s", resources), ex);
        } catch (XMLStreamException ex) {
            logger.error("Error: error while parsing xml", ex);
        }

        return result;
    }

    private UserAndKey getStatisticForUsersAndKeys(XMLEventReader reader) throws XMLStreamException {
        Map<String, Integer> usersMap = new HashMap<>();
        Map<String, Integer> keysMap = new HashMap<>();

        boolean inNode = false;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String name = startElement.getName().getLocalPart();
                if (TAG_NODE.equals(name)) {
                    inNode = true;

                    Attribute userAttribute = startElement.getAttributeByName(new QName(ATTR_USER));
                    usersMap.merge(userAttribute.getValue(), 1, Integer::sum);
                }
                if (inNode && TAG_TAG.equals(name)) {
                    Attribute keyAttribute = startElement.getAttributeByName(new QName(ATTR_KEY));
                    keysMap.merge(keyAttribute.getValue(), 1, Integer::sum);
                }
            }
            if (event.isEndElement() && TAG_NODE.equals(event.asEndElement().getName().getLocalPart())) {
                inNode = false;
            }
        }

        return new UserAndKey(usersMap, keysMap);
    }

}
