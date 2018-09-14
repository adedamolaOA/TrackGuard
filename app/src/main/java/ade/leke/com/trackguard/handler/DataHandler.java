package ade.leke.com.trackguard.handler;

/**
 * Created by SecureUser on 1/29/2016.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

public class DataHandler extends DefaultHandler{

    //list for imported product data
    private ArrayList<TextView> theViews;
    //string to track each entry
    private String currBrand = "";
    //flag to keep track of XML processing
    private boolean isProduct = false;
    //context for user interface
    private Context theContext;
    //constructor
    public DataHandler(Context cont) {
        super();
        theViews = new ArrayList<TextView>();
        theContext = cont;
    }

    //start of the XML document
    public void startDocument () { Log.i("DataHandler", "Start of XML document"); }

    //end of the XML document
    public void endDocument () { Log.i("DataHandler", "End of XML document"); }

    //opening element tag
    public void startElement (String uri, String name, String qName, Attributes atts)
    {
        //handle the start of an element
        //find out if the element is a brand
        if(qName.equals("brand"))
        {
            //set product tag to false
            isProduct = false;
            //create View item for brand display
            TextView brandView = new TextView(theContext);
            brandView.setTextColor(Color.rgb(73, 136, 83));
            //add the attribute value to the displayed text
            String viewText = "Items from " + atts.getValue("name") + ":";
            brandView.setText(viewText);
            //add the new view to the list
            theViews.add(brandView);
        }
//the element is a product
        else if(qName.equals("product"))
            isProduct = true;
    }

    //closing element tag
    public void endElement (String uri, String name, String qName)
    {
        //handle the end of an element
        if(qName.equals("brand"))
        {
            //create a View item for the products
            TextView productView = new TextView(theContext);
            productView.setTextColor(Color.rgb(192, 199, 95));
            //display the compiled items
            productView.setText(currBrand);
            //add to the list
            theViews.add(productView);
            //reset the variable for future items
            currBrand = "";
        }
    }

    //element content
    public void characters (char ch[], int start, int length)
    {
        //process the element content
        //string to store the character content
        String currText = "";
//loop through the character array
        for (int i=start; i<start+length; i++)
        {
            switch (ch[i]) {
                case '\\':
                    break;
                case '"':
                    break;
                case '\n':
                    break;
                case '\r':
                    break;
                case '\t':
                    break;
                default:
                    currText += ch[i];
                    break;
            }
        }
//prepare for the next item
        if(isProduct && currText.length()>0)
            currBrand += currText+"\n";
    }

    public ArrayList<TextView> getData()
    {
        //take care of SAX, input and parsing errors
        try
        {
            //set the parsing driver
            System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
            //create a parser
            SAXParserFactory parseFactory = SAXParserFactory.newInstance();
            SAXParser xmlParser = parseFactory.newSAXParser();
            //get an XML reader
            XMLReader xmlIn = xmlParser.getXMLReader();
            //instruct the app to use this object as the handler
            xmlIn.setContentHandler(this);
            //provide the name and location of the XML file **ALTER THIS FOR YOUR FILE**
            URL xmlURL = new URL("http://mydomain.com/mydata.xml");
            //open the connection and get an input stream
            URLConnection xmlConn = xmlURL.openConnection();
            InputStreamReader xmlStream = new InputStreamReader(xmlConn.getInputStream());
            //build a buffered reader
            BufferedReader xmlBuff = new BufferedReader(xmlStream);
            //parse the data
            xmlIn.parse(new InputSource(xmlBuff));
        }
        catch(SAXException se) { Log.e("AndroidTestsActivity",
                "SAX Error " + se.getMessage()); }
        catch(IOException ie) { Log.e("AndroidTestsActivity",
                "Input Error " + ie.getMessage()); }
        catch(Exception oe) { Log.e("AndroidTestsActivity",
                "Unspecified Error " + oe.getMessage()); }
        //return the parsed product list
        return theViews;
    }
}
