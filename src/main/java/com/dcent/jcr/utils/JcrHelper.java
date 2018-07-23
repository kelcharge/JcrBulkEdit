package com.dcent.jcr.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import javax.jcr.Binary;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import org.apache.jackrabbit.commons.JcrUtils;

/**
 *
 * @author kel
 */
public final class JcrHelper {
    
    //This will return a list of nodes from the JCR
    public static List<Node> getNodes(Session session, String property, String value) throws Exception
    {   
        //The list we will be returning
        ArrayList<Node> out = new ArrayList<Node>();
        
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        QueryResult result = null;

        //build a jcr-sql2 query
        String sqlStatement = "SELECT * FROM [nt:unstructured] AS s WHERE s.[" + property + "] LIKE '%" + value + "%' AND ISDESCENDANTNODE('/content')";
        Query sqlQuery = queryManager.createQuery(sqlStatement, "JCR-SQL2");
        result = sqlQuery.execute();

        NodeIterator nodeIterator = result.getNodes();

        while(nodeIterator.hasNext())
        {
            Node n = nodeIterator.nextNode();
            out.add(n);
            System.out.println("Path: " + n.getPath() + "; Type: " + n.getProperty("sling:resourceType").getString());
        }
                
        return out;
    }
    
    //Returns a new session to the JCR
    public static Session getSession(String host, String username, String password) throws LoginException, NoSuchWorkspaceException, RepositoryException
    {
        Repository repo = JcrUtils.getRepository(host);
        Session session = repo.login(new SimpleCredentials(username, password.toCharArray()), "crx.default");
        
        return session;
    }
    
    public static void writeBinary(Session session, String data, String location) {
        PipedInputStream pis = null;
        try {
            pis = new PipedInputStream();
            PipedOutputStream pos = new PipedOutputStream(pis);
            Executors.newSingleThreadExecutor().submit(new Runnable() {         
            @Override
            public void run() {
                    try {
                    OutputStreamWriter writer = new OutputStreamWriter(pos);
                    writer.write(data);
                    writer.close();
                    pos.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
            }
            });
            Binary binary = null;
            binary = session.getValueFactory().createBinary(pis);
            session.getNode(location + "/jcr:content").setProperty("jcr:data", binary);
            session.save();
        } catch (RepositoryException | IOException e) {
            System.out.println(e.getMessage());
        }
      
   }
}
