package ru.nsu.korovina.services.parsers;

import generated.Node;
import ru.nsu.korovina.models.UserAndKey;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JaxbOsmXmlParser implements Parser {
    private static final Logger logger = LoggerFactory.getLogger(JaxbOsmXmlParser.class);
    private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    private static final String TAG_NODE = "node";

    @Override
    public UserAndKey parse(String resources) {
        UserAndKey result = null;

        try (InputStream bzIs = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(resources)))) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Node.class);
            XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(bzIs);
            result = getStatisticForUsersAndKeys(xmlEventReader, jaxbContext);

        } catch (FileNotFoundException ex) {
            logger.error(String.format("Error: file %s was not found", resources), ex);
        } catch (IOException ex) {
            logger.error(String.format("Error: input exception with file: %s", resources), ex);
        } catch (JAXBException ex) {
            logger.error("Error: error with JAXB", ex);
        } catch (XMLStreamException ex) {
            logger.error("Error: error while parsing xml", ex);
        }

        return result;
    }

    private UserAndKey getStatisticForUsersAndKeys(XMLEventReader reader, JAXBContext context) throws XMLStreamException, JAXBException {
        Map<String, Integer> usersMap = new HashMap<>();
        Map<String, Integer> keysMap = new HashMap<>();

        while (reader.hasNext()) {
            XMLEvent xmlEvent = reader.peek();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                if (startElement.getName().getLocalPart().equals(TAG_NODE)) {
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    Node node = (Node) unmarshaller.unmarshal(reader);
                    usersMap.merge(node.getUser(), 1, Integer::sum);
                    node.getTag().forEach(it -> {
                        keysMap.merge(it.getK(), 1, Integer::sum);
                    });
                    continue;
                }
            }
            reader.nextEvent();
        }

        return new UserAndKey(usersMap, keysMap);
    }
}
