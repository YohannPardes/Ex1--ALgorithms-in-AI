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

    void extract_data(String path, Network created_network){
        created_network.inputFile = path;
        try {
            File file = new File(path);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            // Extract nodes
            extract_variables("VARIABLE", document, created_network);

            // Extracting CPTS
            extract_CPT("DEFINITION", document, created_network);

            // updating the network graph for parents, child relation
            created_network.Update_children();
            created_network.create_factors();

        } catch(ParserConfigurationException e){
            throw new RuntimeException(e);
        } catch(IOException e){
            throw new RuntimeException(e);
        }catch(SAXException e){
            throw new RuntimeException(e);
        }
    }

    void extract_variables(String TagName, Document document, Network created_network){
        // Extracting variables
        NodeList nList = document.getElementsByTagName(TagName);
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                NetNode created_node = new NetNode(
                        eElement.getElementsByTagName("NAME").item(0).getTextContent());
                created_network.nodes.add(created_node); // Adding a node to the network

                int j = 0;
                try { // trying to go over all the "OUTCOME" tag until we reach the end of the "GIVEN" tags

                    while (true){
                        created_node.add_outcome(eElement.getElementsByTagName("OUTCOME") // creating a new node each time
                                .item(j).getTextContent());
                        j += 1;
                    }
                }
                catch (NullPointerException e){
                }
            }
        }
    }

    CPT extract_CPT(String TagName, Document document, Network created_network){

        NodeList nList = document.getElementsByTagName(TagName); // extracting the tag <TAGNAME>
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                NetNode current_node = created_network.find_node(eElement.getElementsByTagName("FOR").item(0).getTextContent());
                int j = 0;
                try { // trying to go over all the "GIVEN" tag until we reach the end of the "GIVEN" tags

                    while (true){
                        // getting all the parents of a node
                        current_node.Parents.add(created_network.find_node(eElement.getElementsByTagName("GIVEN").item(j).getTextContent()));
                        j++;
                    }
                }
                catch (NullPointerException e){
                }
                // CPT values extraction
                current_node.CPT.extracting_values_from_String(
                        eElement.getElementsByTagName("TABLE").item(0).getTextContent()
                );
            }
        }
        return null;
    }
}
