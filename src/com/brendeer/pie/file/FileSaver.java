package com.brendeer.pie.file;

import com.brendeer.pie.core.FilterNode;
import com.brendeer.pie.core.FilterProgram;
import com.brendeer.pie.core.Pin;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This static class can save a FilterProgram to an xml-file
 * @author Erik
 */
public class FileSaver {

	public static boolean saveXML(FilterProgram program, File file) {

		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fac.newDocumentBuilder();

			Document d;
			d = builder.newDocument();

			//root element
			Element root = d.createElement("program");
			d.appendChild(root);

			synchronized (program.getNodeLock()) {
				List<FilterNode> nodeList = program.getNodes();

				//save all the filters
				Element filters = d.createElement("filters");
				root.appendChild(filters);
				saveFilters(d, filters, nodeList);

				//save all the connections
				Element connections = d.createElement("connections");
				root.appendChild(connections);
				saveConnections(d, connections, nodeList);

			}
			//save the document
			TransformerFactory tfac = TransformerFactory.newInstance();
			Transformer transformer = tfac.newTransformer();
			DOMSource source = new DOMSource(d);
			StreamResult result = new StreamResult(file);

			transformer.transform(source, result);
			return true;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private static void saveFilters(Document d, Element xmlFiltersRootNode, List<FilterNode> nodeList) {
		for (FilterNode filterNode : nodeList) {

			//fetch data
			String name = filterNode.getPluginName();
			Point2D.Float pos = filterNode.getPos();
			String options = filterNode.getFilter().getOptions().serialize();

			//create the xml node for this filter
			Element xmlFilterNode = d.createElement("filter");

			//convert to xml nodes
			xmlFilterNode.appendChild(xmlText(d, "name", name));
			xmlFilterNode.appendChild(xmlFloatPoint(d, "pos", pos));
			xmlFilterNode.appendChild(xmlText(d, "options", options));

			//add the data-filled xml-node to the filters-root
			xmlFiltersRootNode.appendChild(xmlFilterNode);
		}
	}

	private static void saveConnections(Document d, Element xmlConnectionsRootNode, List<FilterNode> nodeList) {
		for (FilterNode filterNode : nodeList) {
			for (Pin pin : filterNode.getFilter().getInPins()) {

				Pin sourcePin = pin.getSource();
				if (sourcePin != null) { //iterate over all connected in-going pins

					//create the xml node for this connection
					Element xmlConnectionNode = d.createElement("connection");
					
					//add the pin data
					xmlConnectionNode.appendChild(xmlPin(d, "pin1", sourcePin, nodeList));
					xmlConnectionNode.appendChild(xmlPin(d, "pin2", pin, nodeList));

					//add the data-filled xml-node to the connections-root
					xmlConnectionsRootNode.appendChild(xmlConnectionNode);
				}
			}
		}
	}

	private static Node xmlPin(Document d, String pinName, Pin pin, List<FilterNode> nodeList) {
		Element xmlPin = d.createElement(pinName);
		
		int filterID = -1;
		for (int n = 0; n < nodeList.size(); n++) {
			if (nodeList.get(n).getFilter() == pin.getFilter()) {
				filterID = n;
				break;
			}
		}
		int index = pin.getIndex();
		
		xmlPin.appendChild(xmlText(d, "filter", Integer.toString(filterID)));
		xmlPin.appendChild(xmlText(d, "index", Integer.toString(index)));
		
		return xmlPin;
	}

	private static Element xmlFloatPoint(Document d, String name, Point2D.Float point) {
		Element p = d.createElement(name);
		p.appendChild(xmlText(d, "x", Float.toString(point.x)));
		p.appendChild(xmlText(d, "y", Float.toString(point.y)));
		return p;
	}

	private static Element xmlText(Document d, String name, String value) {
		Element textNode = d.createElement(name);
		textNode.appendChild(d.createTextNode(value));
		return textNode;
	}
}
