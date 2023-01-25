package com.utilsrv.doc.batch.convert;

import com.utilsrv.doc.DocConstants;
import org.apache.commons.io.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class HtmlDocParserHandler extends DefaultHandler {
    private File htmlDocFile;
    private XMLStreamWriter subPageWriter;
    private XMLStreamWriter mainPageWriter;
    private Stack<String> elementStack = new Stack<>();
    private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
    private String pageId;
    private int pageCount;

    public static int parseXml(File file) throws ParserConfigurationException, IOException, SAXException {
        HtmlDocParserHandler instance = new HtmlDocParserHandler(file);
        instance.parseXml();
        return instance.pageCount;
    }

    private HtmlDocParserHandler(File file) throws IOException {
        this.htmlDocFile = file;
        this.mainPageWriter = newXmlStreamWriter(new File(file.getParent(), DocConstants.HTML_MAIN_PAGE), null);
        FileUtils.forceMkdir(new File(file.getParent(), "pages"));
    }

    private XMLStreamWriter newXmlStreamWriter(File file, XMLStreamWriter lastWriter) {
        try {
            XMLStreamWriter writer = null;
            if (lastWriter != null) {
                lastWriter.close();
            }
            OutputStream outputStream = new FileOutputStream(file);
            writer = xmlOutputFactory.createXMLStreamWriter(
                    new OutputStreamWriter(outputStream, "utf-8"));
            return writer;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void parseXml() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory f = SAXParserFactory.newInstance();
        f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        f.setFeature("http://xml.org/sax/features/validation", false);
        SAXParser saxParser= f.newSAXParser();
        saxParser.parse(this.htmlDocFile, this);
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            this.mainPageWriter.writeStartDocument();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementStack.push(qName);
        writeStartElement(qName, attributes);
        // TODO if a new page, start a new writer
        // TODO add helper js template and update in reader
    }

    private void writeStartElement(String qName, Attributes attributes) {
        Map<String, String> attrs = new LinkedHashMap<>();
        String htmlClass = null;
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            String value = attributes.getValue(i);
            attrs.put(name, value);
            if ("class".equalsIgnoreCase(name)) {
                htmlClass = value;
            }
            if ("id".equalsIgnoreCase(name)) {
                pageId = value;
            }
        }
        XMLStreamWriter writer = writer(htmlClass, pageId);
        try {
            writer.writeStartElement(qName);
            for (String name : attrs.keySet()) {
                String value = attrs.get(name);
                writer.writeAttribute(name, value);
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            writer(null, null).writeCharacters(ch, start, length);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        try {
            if ("/html/head/title".equals(path())) {
                mainPageWriter.writeComment("$main_page_script");
            }
            writer(null, null).writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        elementStack.pop();
    }

    private XMLStreamWriter writer(String classAttr, String id) {
        if (path().equals("/html") || path().startsWith("/html/head") || path().equals("/html/body")) {
            return mainPageWriter;
        }
        else {
            if ("page".equalsIgnoreCase(classAttr) && id != null && id.matches("page_\\d+")) {
                pageCount++;
                subPageWriter = newXmlStreamWriter(FileUtils.getFile(this.htmlDocFile.getParent(), "pages", id + ".html"), subPageWriter);
            }
            return subPageWriter;
        }
    }

    private String path() {
        String path = "/" + String.join("/", elementStack);
        return path.toLowerCase();
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            mainPageWriter.writeEndDocument();
            mainPageWriter.close();
            if (subPageWriter != null) {
                subPageWriter.close();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }


}
