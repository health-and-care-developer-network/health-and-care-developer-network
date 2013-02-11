/*
  Copyright 2012 Damian Murphy <murff@warlock.org>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.warlock.tk.internalservices.distributor.xdradapter;
import org.warlock.tk.boot.ToolkitSimulator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.Socket;
import javax.net.SocketFactory;
/**
 * EXPERIMENTAL singleton adapter for converting an ITK DistributionEnvelope to an
 * XDR ProvideAndRegister message, and sending it.
 * 
 * Note that whilst this works, it is incomplete pending decisions on metadata.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ITKXDRAdapter {

    private static final int IHETOITK = 1;
    private static final int ITKTOIHE = 2;
    private static final int ITKTOIHEXOP = 3;
    
    private static ToolkitSimulator simulator = null;
    private static ITKXDRAdapter instance = new ITKXDRAdapter();
    
    private static int direction = 0;
    
    private static String responseLog = null;
    /**
     * @param args the command line arguments
     */

    private ITKXDRAdapter() {}
    
    public static ITKXDRAdapter getInstance(ToolkitSimulator t) 
    {
        if (simulator == null) {
            simulator = t;
        }
        responseLog = simulator.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.xdradapterresponselog");
        return instance; 
    }
    
    public void sendToXDR(String m, String a)
            throws Exception
    {
        String ct = "multipart/related; type=\"application/xop+xml\";start=\"<http://tempuri.org/0>\";boundary=\"uuid:66605a42-6361-4ec9-8744-e3f9fdf6c6c2+id=2\";start-info=\"application/soap+xml\"";        
        ITKtransmission itk = new ITKtransmission();
        ByteArrayInputStream f = new ByteArrayInputStream(m.getBytes());
        itk.load(f, ct);
        XDRtransmission xdr = new XDRtransmission();
        xdr.convert(itk);
        String o = xdr.serialise();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XDRtransmission.toXopOutputStream(o, a, out);
        send(out.toByteArray(), a);
        return;

    }
    
    private void send(byte[] out, String addr)
            throws Exception
    {
        URL u = new URL(addr);
        Socket s = SocketFactory.getDefault().createSocket(u.getHost(), u.getPort());
        s.getOutputStream().write(out);
        s.getOutputStream().flush();
        if (responseLog != null) {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(responseLog, true);
            int r = 0;
            while ((r = s.getInputStream().read()) != -1) {
                fos.write(r);
            }
            fos.flush();
            fos.close();
        } else {
            while (s.getInputStream().read() != -1) {}
        }
        s.close();
    }

    /*
    
    public static void main(String[] args) {
        // args[0] is input type (ITK or IHE)
        // args[1] is input file
        // args[2] is output file
        
        if (args[0].contentEquals("IHE")) {
            direction = IHETOITK;
        } else {
            if (args[0].contentEquals("ITK")) {
                direction = ITKTOIHE;
            } else {
                if (args[0].contentEquals("ITKXOP")) {
                    direction = ITKTOIHEXOP;
                } else {
                    System.err.println("Unrecognised input type");
                    System.exit(1);
                }
            }
        }
        System.setProperty("tks.itkxdradapter.sender.oid", "2.16.840.1.113883.2.1.3.2.4.18.22");
        System.setProperty("tks.itkxdradapter.sender.address", "a:b:c:d");
//        String ct = "text/xml";
        String ct = "multipart/related; type=\"application/xop+xml\";start=\"<http://tempuri.org/0>\";boundary=\"uuid:66605a42-6361-4ec9-8744-e3f9fdf6c6c2+id=2\";start-info=\"application/soap+xml\"";
        try {
            FileInputStream f = new FileInputStream(args[1]);
            if (direction == ITKTOIHEXOP) {
                    ITKtransmission itk = new ITKtransmission();
                    itk.load(f, ct);
                    XDRtransmission xdr = new XDRtransmission();
                    xdr.convert(itk);
                    String o = xdr.serialise();
                    XDRtransmission.toXopOutputStream(o, "http://itktest.example.com/xdrxop", new FileOutputStream(args[2]));
                    return;
            }
            FileWriter w = new FileWriter(args[2]);
            String o = null;
            XDRtransmission xdr = null;
            ITKtransmission itk = null;
            switch (direction) {
                case IHETOITK:
                    xdr = new XDRtransmission();
                    xdr.load(f, ct);
                    itk = new ITKtransmission();
                    itk.convert(xdr);
                    o = itk.serialise();
                    break;
                    
                case ITKTOIHE:
                    itk = new ITKtransmission();
                    itk.load(f, ct);
                    xdr = new XDRtransmission();
                    xdr.convert(itk);
                    o = xdr.serialise();
                    break;                    
            }
            f.close();
            w.write(o);
            w.flush();
            w.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
     */ 
}
