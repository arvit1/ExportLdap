package com.etn;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.EntrySorter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import javax.net.SocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Created by nushi on 1/9/2018.
 */
public class ExportLdap2 {
    static int i = 0;
    public static void main(String[] args) throws LDAPException, IOException {
        TrustManager[] trustManagers = new TrustManager[1];
        trustManagers[0] = new TrustAllTrustManager();

        LDAPConnection conn = null;


        try {
            SSLUtil sslUtil = new SSLUtil(trustManagers);
            SocketFactory factory
                    = sslUtil.createSSLSocketFactory();

            conn = new LDAPConnection(factory, "aol.devel-etdirectory.net", 636);
            conn.bind("cn=Manager,dc=etdirectory,dc=net", "$3v-3\\pIjD");

            sortedSearch("dc=etdirectory,dc=net","objectClass=*", conn, new String[] {"*"}, "backup.ldif");
            sortedSearch("dc=etdirectory,dc=net","(!(objectClass=person))", conn, new String[] {"dn", "dc", "ou", "objectClass", "description", "cn", "oauthClientSecret", "oauthClientIdentifier", "oauthGrantType", "refreshTokenValidity", "oauthScope", "oauthTokenValidity"}, "oauth2.ldif");
        } catch (LDAPException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        }
    }

    public static void sortedSearch(String baseDN, String filter, LDAPConnection connection, String[] attributes, String fileName) throws LDAPException, IOException {
        SearchRequest searchRequest = new SearchRequest(baseDN, SearchScope.SUB, filter, attributes);
        SearchResult searchResult = connection.search(searchRequest);
        EntrySorter entrySorter = new EntrySorter();
        SortedSet<Entry> sortedEntries = entrySorter.sort(searchResult.getSearchEntries());
        Iterator<Entry> iterator = sortedEntries.iterator();

        Charset charset = Charset.forName("UTF-8");
        Path filePath = Paths.get("src/main/resources", fileName);
        BufferedWriter writer = Files.newBufferedWriter(filePath, charset);
        StringBuilder st = new StringBuilder();

         while (iterator.hasNext()) {
             Entry entry = iterator.next();
             //System.out.println(entry.toLDIFString());
             saveToFile(entry.toLDIFString(), st);
         }

        writer.write(st.toString());
        writer.close();
    }

    public static void saveToFile(String entry, StringBuilder sb) throws IOException {
        sb.append(entry);
        sb.append("\n");
        System.out.println(i++);
    }
}
