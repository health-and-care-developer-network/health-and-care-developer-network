/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.warlock.tk.internalservices.testautomation;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
/**
 *
 * @author DAMU2
 */
public class BasicMessageIdCorrelator 
    implements AsynchronousLogCorrelator
{
    @Override
    public boolean correlate(File log, Message request) 
            throws Exception
    {
        return correlates(log, request.getMessageId());
    }

    private boolean correlates(File f, String msgid)
            throws Exception
    {
        // Fast scan by looking for WS-Addressing "RelatesTo" and the
        // original message id on the same line
        //
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("RelatesTo")) {
                if (line.contains(msgid)) {
                    br.close();
                    return true;
                }
            }
        }
        return false;
    }
    
}
