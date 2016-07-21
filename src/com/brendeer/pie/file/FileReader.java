package com.brendeer.pie.file;

import com.brendeer.pie.core.FilterNode;
import com.brendeer.pie.core.FilterProgram;
import com.brendeer.pie.core.PluginContainer;
import com.brendeer.pie.filter.Filter;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This static class loads xml-files to FilterPrograms
 * @author Erik
 */
public class FileReader {

	public static FilterProgram decodeXML(File xmlFile) {
		try {
			return decodeXML(new FileInputStream(xmlFile), xmlFile);
		} catch (FileNotFoundException ex) {
		}
		return null;
	}

	public static FilterProgram decodeXML(String xml) {
		return decodeXML(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), null);
	}

	public static FilterProgram decodeXML(InputStream xml, File origin) {
		FilterProgram filterProgram = new FilterProgram(origin);

		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fac.newDocumentBuilder();

			//list of all loaded filters so they can get accessed later for connecting
			List<Filter> filterList = new ArrayList<>();

			Document doc = builder.parse(xml);
			Node root = doc.getChildNodes().item(0); //<program>

			//load all the filter objects
			NodeList filters = findChildByName(root, "filters").getChildNodes();
			loadFilters(filters, filterProgram, filterList);

			//now load all the connections between them
			NodeList connections = findChildByName(root, "connections").getChildNodes();
			loadConnections(connections, filterProgram, filterList);

		} catch (Exception ex) {
			System.err.println("ERROR: ");
			ex.printStackTrace();
		}

		return filterProgram;
	}

	private static void loadFilters(NodeList filters, FilterProgram filterProgram, List<Filter> filterList) {
		for (int filterIndex = 0; filterIndex < filters.getLength(); filterIndex++) {
			Node xmlFilterNode = filters.item(filterIndex);
			if (!xmlFilterNode.getNodeName().equals("filter")) {
				continue;
			}
			//read the xml data
			String name = findChildByName(xmlFilterNode, "name").getTextContent();
			Point2D.Float pos = xmlFloatPoint(findChildByName(xmlFilterNode, "pos"));
			String options = findChildByName(xmlFilterNode, "options").getTextContent();

			//create the filter object
			Filter filter = PluginContainer.getPlugin(name).createInstance();

			//set its options
			filter.getOptions().deSerialize(options);

			//create the filterNode-container
			Point2D.Float size = filter.getDisplaySize();
			FilterNode filterNode = new FilterNode(name, filter, pos.x, pos.y, size.x, size.y);

			//finished!
			filterProgram.addNode(filterNode);
			filterList.add(filter);
		}
	}

	private static void loadConnections(NodeList connections, FilterProgram filterProgram, List<Filter> filterList) {
		for (int connIndex = 0; connIndex < connections.getLength(); connIndex++) {
			Node xmlConnectionNode = connections.item(connIndex);
			if (!xmlConnectionNode.getNodeName().equals("connection")) {
				continue;
			}

			//first read all the xml data
			Node xmlPin1 = findChildByName(xmlConnectionNode, "pin1");
			Node xmlPin2 = findChildByName(xmlConnectionNode, "pin2");

			//convert the read xml data
			int[] pin1 = xmlPin(xmlPin1);
			int[] pin2 = xmlPin(xmlPin2);

			filterProgram.connect(
					filterList.get(pin1[0]).getOutPins().get(pin1[1]),
					filterList.get(pin2[0]).getInPins().get(pin2[1]), null);
		}
	}

	private static final Point2D.Float xmlFloatPoint(Node point) {
		float x = Float.valueOf(findChildByName(point, "x").getTextContent());
		float y = Float.valueOf(findChildByName(point, "y").getTextContent());
		return new Point2D.Float(x, y);
	}

	private static final int[] xmlPin(Node pin) {
		int filter = Integer.valueOf(findChildByName(pin, "filter").getTextContent());
		int index = Integer.valueOf(findChildByName(pin, "index").getTextContent());
		return new int[]{filter, index};
	}

	static final Node findChildByName(Node parent, String childName) {
		NodeList childs = parent.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node n = childs.item(i);
			if (n.getNodeName().equals(childName)) {
				return n;
			}
		}
		System.err.println("No child called \"" + childName + "\" in a list of " + childs.getLength() + " items.");
		return null;
	}
}
