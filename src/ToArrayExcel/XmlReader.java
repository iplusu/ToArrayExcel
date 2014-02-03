package ToArrayExcel;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class XmlReader {
    private String saveFile;
    private File file;
    private DocumentBuilder docBuilder;
    private Document doc;

    public String nssPath;
    public ArrayList<ThroughputArraySet> throughputSet;
    public ArrayList<TrustArraySet> trustSet;
    public ArrayList<ParentArraySet> parentSet;

    public XmlReader(String saveFile) {
        try {
            this.saveFile = saveFile;
            nssPath = null;
            throughputSet = null;
            trustSet = null;
            parentSet = null;
            file = new File(saveFile);

            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.parse(file);
            this.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parse() {
        parseNssPath();
        parseThroughput();
        parseParent();
        parseTrust();
    }

    private void parseNssPath() {
        nssPath = new String();
        NodeList rowList = doc.getDocumentElement().getElementsByTagName("nss");
        if (rowList.getLength() == 1) {
            Element e = (Element)rowList.item(0);
            nssPath = e.getAttribute("path");
        } else {
            nssPath = null;
        }
    }

    private void parseParent(){
        parentSet = new ArrayList<ParentArraySet>();
        NodeList rowList = doc.getDocumentElement().getElementsByTagName("parent");
        if (rowList.getLength() > 0) {
            for (int i = 0; i < rowList.getLength(); i++) {
                Element e = (Element)rowList.item(i);
                int transaction = Integer.parseInt(e.getAttribute("transaction"));
                int source = Integer.parseInt(e.getAttribute("source"));
                int target = Integer.parseInt(e.getAttribute("target"));
                
                ParentArraySet pas = null;
                if(!parentSet.isEmpty()){
                    pas = parentSet.get(parentSet.size() - 1);
                    if(pas.isNewTransaction(transaction)){
                        pas = new ParentArraySet(transaction);
                        parentSet.add(pas);
                    }
                } else {
                    pas = new ParentArraySet(transaction);
                    parentSet.add(pas);
                }
                
                if(pas.isSameTransaction(transaction) && !pas.isRouteExist(source, target)){
                    pas.add(source, target);
                }
            }
        }
        else    parentSet = null;
    }

    private void parseThroughput() {
        NodeList rowList = doc.getDocumentElement().getElementsByTagName("throughput");
        if (rowList.getLength() > 0) {
            throughputSet = new ArrayList<ThroughputArraySet>();
            for (int i = 0; i < rowList.getLength(); i++) {
                Element e = (Element)rowList.item(i);
                ThroughputArraySet tas = new ThroughputArraySet();
                tas.add(Integer.parseInt(e.getAttribute("transaction")), Float.parseFloat(e.getAttribute("value")));
                throughputSet.add(tas);
            }
        }
        else    throughputSet = null;
    }

    private void parseTrust() {
        NodeList rowList = doc.getDocumentElement().getElementsByTagName("trust");
        if (rowList.getLength() > 0) {
            trustSet = new ArrayList<TrustArraySet>();
            for (int i = 0; i < rowList.getLength(); i++) {
                Element e = (Element)rowList.item(i);
                TrustArraySet tas = new TrustArraySet(Integer.parseInt(e.getAttribute("source")), Integer.parseInt(e.getAttribute("target")));
                int transaction = Integer.parseInt(e.getAttribute("transaction"));
                long time = Integer.parseInt(e.getAttribute("time"));
                float trustFP = Float.parseFloat(e.getAttribute("trustFP"));
//                float trustLP = Float.parseFloat(e.getAttribute("trustLP"));
                float trustPR = Float.parseFloat(e.getAttribute("trustPR"));
                float overall = Float.parseFloat(e.getAttribute("overall"));
//                tas.addTrustValues(transaction, time, trustFP, trustRH, trustAV, trustLP, trustPR, overall);
                tas.addTrustValues(transaction, time, trustFP, /*trustLP,*/ trustPR, overall);
                trustSet.add(tas);
            }
        }
        else       trustSet = null;
    }
}
