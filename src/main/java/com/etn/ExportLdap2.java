package com.etn;

import com.unboundid.ldap.sdk.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Created by nushi on 1/9/2018.
 */
public class ExportLdap2 {
    static int i = 0;
    public static void main(String[] args) throws LDAPException, IOException {
        LDAPConnection c = new LDAPConnection();
        c.connect("172.24.1.17", 389);
        BindResult bindResult = c.bind("uid=admin,ou=system", "secret");
        sortedSearch("dc=example,dc=com","objectClass=*", c, new String[] {"*"}, "backup.ldif");
        sortedSearch("cn=oauth2,ou=schema","objectClass=*", c, new String[] {"*"}, "oauth2.ldif");
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
