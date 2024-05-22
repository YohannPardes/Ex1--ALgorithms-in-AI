import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class XMLParsing {

    void extract_data(String path){
        System.out.println(path);
        try {
            File file = new File(path);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            System.out.println(document);

            System.out.println("Root element :" + document.getDocumentElement().getNodeName());
            // Extract nodes
            extract_variables("VARIABLE", document);

            // Extracting CPTS
            extract_CPT("DEFINITION", document);

        } catch(ParserConfigurationException e){
            throw new RuntimeException(e);
        } catch(IOException e){
            throw new RuntimeException(e);
        }catch(SAXException e){
            throw new RuntimeException(e);
        }
    }

    void extract_variables(String TagName, Document document){
        // Extracting variables
        NodeList nList = document.getElementsByTagName(TagName);
        System.out.println("----------------------------");
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                System.out.println("Name : "
                        + eElement.getElementsByTagName("NAME")
                        .item(0).getTextContent());
                System.out.println("Outcome 1 : "
                        + eElement.getElementsByTagName("OUTCOME")
                        .item(0).getTextContent());
                System.out.println("Outcome 2 : "
                        + eElement.getElementsByTagName("OUTCOME")
                        .item(1).getTextContent());
            }
        }
    }

    CPT extract_CPT(String TagName, Document document){

        NodeList nList = document.getElementsByTagName(TagName); // extracting the tag <TAGNAME>
        System.out.println("\n\n----------------------------");
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                System.out.println("FOR : "
                        + eElement.getElementsByTagName("FOR")
                        .item(0).getTextContent());
                int j = 0;
                try { // trying to go over all the "GIVEN" tag until we reach the end of the "GIVEN" tags

                    while (true){
                        System.out.println("given : "
                                + eElement.getElementsByTagName("GIVEN")
                                .item(j).getTextContent());
                        j += 1;
                    }
                }
                catch (NullPointerException e){
                    ;
                }
//                System.out.println("Table "
//                        + 2 +"X" + (j+1) + ": "
//                        + eElement.getElementsByTagName("TABLE")
//                        .item(0).getTextContent());

                // creating a cpt
                new CPT(eElement.getElementsByTagName("TABLE")
                        .item(0).getTextContent());
            }
        }
        return null;
    }
}
