/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.warlock.tk.internalservices.testautomation;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.StringReader;
import org.xml.sax.InputSource;
/**
 *
 * @author DAMU2
 */
public class PHAsynchronousXPathAssertionPassFailCheck 
    extends PHSynchronousXPathAssertionPassFailCheck
{
    public PHAsynchronousXPathAssertionPassFailCheck() 
            throws Exception
    {
        super();
    }

    @Override
    public boolean passed(Script s, InputStream in)
            throws Exception
    {
        String responseBody = getResponseBody(in);
        String hl7v2response = getHL7v2(responseBody);
        InputSource is = new InputSource(new StringReader(hl7v2response));
        return doChecks(s, is);
    }
    
    @Override
    protected String getResponseBody(InputStream in)
            throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        boolean body = false;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (body) {
                if (line.startsWith("************ END OF INBOUND MESSAGE **************")) {
                    break;
                }
                sb.append(line);
                sb.append("\r\n");
            } else {
                if (line.trim().length() == 0) {
                    body = true;
                    continue;
                }
            }
        }        
        return sb.toString();
    }
    
}
