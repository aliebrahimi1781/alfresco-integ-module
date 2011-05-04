package controllers;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import play.libs.WS.WSRequest;
import play.mvc.Before;
import play.mvc.Controller;




public class Application extends Controller {
//	public static String url  = "http://10.10.10.205:8080/alfresco/s/cmis";
	public static String url  = "http://localhost:8080/alfresco/s/cmis";
	public static Session session ;
	
	
    public static void index() {
//    	WSRequest req = WS.url(url);
//    	setCredentials(req);
//    	HttpResponse res = req.get();
//    	String string = res.getString();
    	
//    	WSRequest url = WS.url("http://www.google.com");
//    	HttpResponse res = url.get();
//    	String string = res.getString();
    	
    	
//    	HttpResponse response = WS.url("http://search.yahoo.com/search?p=%s&pstart=1&b=%s", "Google killed me", "30").get();
//        if( response.getStatus() == 200 ) {
//           html = response.getString();
//        }
    	List docs = new ArrayList(); 

    	Session session = (Session) renderArgs.get("session");
    	ItemIterable<QueryResult> results = session.query("SELECT * FROM test:tmc where test:org = 'cio'  ", false);
    	StringBuffer buff = new StringBuffer();

    	if(results != null){
	    	for(QueryResult hit: results) {  
	    	    for(PropertyData<?> property: hit.getProperties()) {
	    	        String queryName = property.getQueryName();
	    	        Object value = property.getFirstValue();
	    	        buff.append("[").append(queryName).append("]    [").append(value).append("]<br/>");
	//    	        System.out.println(queryName + ": " + value);
	    	    }
	    	    buff.append("--------------------------------------<br/>");
	//    	    System.out.println("--------------------------------------");
	    	}
    	}
    	
    	
    	String myType = "D:test:tmc";
    	// get the query name of cmis:objectId
    	ObjectType type = session.getTypeDefinition(myType);
    	System.out.println(type.getQueryName());
    	PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
    	String objectIdQueryName = objectIdPropDef.getQueryName();

    	String queryString = "SELECT cmis:name , " + objectIdQueryName + " FROM " + type.getQueryName();

    	// execute query
//    	 results = session.query(queryString, false);
    	results = session.query("SELECT cmis:name, cmis:objectId FROM test:tmc where test:org = 'gdt'  ", false);
    	for (QueryResult qResult : results) {
    	   String objectId = qResult.getPropertyValueByQueryName(objectIdQueryName);
    	   Document doc = (Document) session.getObject(session.createObjectId(objectId));
    	   
    	   docs.add(doc);
    	   doc.getContentStream().getStream();
    	   List<Property<?>> properties = doc.getProperties();

    	   //    	   doc.getName()
    	}
    	String text = buff.toString();
    	render(docs, text, type);
    }
    
    public static void show(Integer cpr){
    	renderTemplate("Application/index.html", cpr);
    }
    
    private static void setCredentials(WSRequest  req){
    	req.username = "admin";
    	req.password= "admin";
    }
    
    public static void add(Integer cpr){
    	
    	renderTemplate("Application/index.html", cpr);
    }
    
    @Before
    private static void connectToAlfresco(){
//    	if(session != null) return;
    	
    	CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    	CookieHandler.setDefault(cm);
    	
    	// default factory implementation
    	SessionFactory factory = SessionFactoryImpl.newInstance();
    	Map<String, String> parameter = new HashMap<String, String>();

    	// user credentials
    	parameter.put(SessionParameter.USER, "admin");
    	parameter.put(SessionParameter.PASSWORD, "admin");

    	// connection settings
    	parameter.put(SessionParameter.ATOMPUB_URL, url);
    	parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    	// localhost repository
    	parameter.put(SessionParameter.REPOSITORY_ID, "371554cd-ac06-40ba-98b8-e6b60275cca7");
//    	parameter.put(SessionParameter.REPOSITORY_ID, "a198c234-0f99-4202-b1fb-a4ef42efb2fb");
    	
    	List<Repository> repositories = factory.getRepositories(parameter);
    	Session se = repositories.get(0).createSession();
    	
    	renderArgs.put("session", se);
//    	Folder rootFolder = session.getRootFolder();
//    	rootFolder.getn
//    	ItemIterable<CmisObject> children = session.getRootFolder().getChildren();
//    	renderArgs.put("children", children);
    	
    	// create session
//    	Session session = factory.createSession(parameter);
    }
    
    public static void getChild(String path){
    	Session session = (Session) renderArgs.get("session");
    	
    }
    

}