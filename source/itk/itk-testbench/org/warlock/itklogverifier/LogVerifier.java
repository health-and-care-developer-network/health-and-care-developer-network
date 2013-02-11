/*
  Copyright 2012  Damian Murphy <murff@warlock.org>

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
package org.warlock.itklogverifier;
import java.security.MessageDigest;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
/**
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class LogVerifier {

    private static final String DECOMP_COMMENT = "OK, so you can decompile the verifier. Now go and fix your code properly rather than trying to defraud the accreditation process.";

    private static final String[] AP = {"afe3cc9d-30e1-4583-a06f-e99294a9b1b7",
					"7EE91BAE-6711-4B15-9622-2957C7A95F5D",
					"55a480a8-3a55-4e13-a365-8a07e79659b9",
					"720d777c-5470-402f-8494-aae073102c12"};

    private static final String[] CP = {"f5697922-e8f4-4bbc-baed-d777a3e0a674",
					"807e6665-e72e-4fde-a31c-de1cef75d1ea",
					"E0F74571-7D6F-4097-8D91-519AEB394A11",
					"cc366dcf-3eb8-4166-9193-d6e95ec99b70"};

    private static final String[] JP = {"0a5d215f-fb83-41bb-b823-3fcdba6c6b56",
					"C884574D-ACB4-4C93-97A0-E6EBB8142A2C",
					"D95F36F3-7A6D-4FF0-9554-3DF8789D380D",
					"627FD1D8-9FFB-489F-AF12-E3B8C02AB2C6"};

    private static final String[] KP = {"A6B0723C-255A-4D01-91B8-F59D2EBA9089",
                                        "27329342-8eff-4e2c-a9a4-eb9171c72cc8",
                                        "0fa2f4e3-e4aa-424a-bc7b-031991f5425f",
                                        "9E90F3CE-8D15-48CD-AAA7-CBEC0DCF01FC"};

    private static final String[] MP = {"18833B52-2682-4D53-97D2-D39313672BB3",
					"C17D6448-8A84-4F55-9FE7-CCD43AD5F1C8",
					"0800b23e-09b5-4616-8dee-0c170a94b8ec",
					"8148862B-57AB-4156-ABC1-26EDAEFD63FF"};

    private static final String[] QP = {"417B7407-9FCE-487A-BC55-FC0419626AD3",
					"5ade2bde-30b1-45dc-914c-d52d0adad0fb",
					"9fba94c7-d50f-467f-92be-8b31ae716f4c",
					"785854B1-41F6-4E73-AD6F-8F7AB3D9C0A9"};

    private static final String[] SP = {"6a3e09b4-28ba-471d-b9fd-ecaf95d444ac",
                                        "C47F9603-E208-4124-8A8D-474B41D5CFC5",
                                        "564609FB-86C7-4960-A0D3-38BEC55BE073",
                                        "c677c749-228c-4ded-83b8-8e00352f71c8"};

    private static final String[] VP = {"83363db8-d968-421f-b073-1da2682b7ac4",
					"38BB67A8-CC8D-4E3F-BC42-266334395BEC",
					"2eb1b9ba-d13d-4bfb-aa34-dbbe16794ce0",
					"14e6c713-78bd-4009-835f-ea8834746245"};

    private static final String KEYALIAS = "SubmissionVerifier";
    private static final String KEYSTORE = "itkaccreditation.keystore";
    public static final String SIGNATUREEXTENSION = ".signature";
    private static final String SIGALGORITHM = "SHA1withDSA";

    private PrivateKey privateKey = null;
    private Certificate certificate = null;

    private static Exception bootException = null;
    private static LogVerifier me = new LogVerifier();


    private LogVerifier() {
        String keypass = makePass(KP, "key");
        String storepass = makePass(SP, "store");

        if (keypass == null) {
            return;
        }
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance("JKS");
            InputStream is = getClass().getResourceAsStream(KEYSTORE);
            keystore.load(is, storepass.toCharArray());
        }
        catch (Exception e) {
            bootException = e;
            System.err.println("FATAL: Cannot load keystore: " + e.toString());
            return;
        }
        try {
            certificate = keystore.getCertificate(KEYALIAS);
            privateKey = (PrivateKey)keystore.getKey(KEYALIAS, keypass.toCharArray());
        }
        catch (Exception e) {
            bootException = e;
            System.err.println("FATAL: Cannot read keystore: " + e.toString());
        }
    }

    private String makePass(String[] src, String t) {

        StringBuilder sb = new StringBuilder(src[0]);
        sb.append("-");
        sb.append(src[1]);
        sb.append("-");
        sb.append(src[2]);
        sb.append("-");
        sb.append(src[3]);
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            String in = sb.toString();
            byte[] input = in.getBytes(java.nio.charset.Charset.forName("US-ASCII"));
            byte[] digest = sha1.digest(input);
            StringBuilder o = new StringBuilder();
            for (byte b : digest) {
                o.append(Integer.toHexString(0xFF & b));
            }
            return o.toString();
        }
        catch (Exception e) {
            bootException = e;
            System.err.println("FATAL: Cannot make internal " + t +  "password: " + e.toString());
        }
        return null;
    }

    public static LogVerifier getInstance()
        throws Exception
    {
        if (bootException != null) {
            throw bootException;
        }
        return me;
    }

    public void makeSignature(String filename)
        throws Exception
    {
        // Make the output filename, from the filename + SIGNATUREEXTENSION
        // Open and read the file. Strip any whitespace.
        // Make a signature, and initialise it with the privateKey
        // Convert the whitespace-stripped file to a byte array
        // and sign it

        if (bootException != null) {
            throw bootException;
        }
        StringBuilder outfile = new StringBuilder(filename);
        outfile.append(SIGNATUREEXTENSION);
        byte[] output = calculateSignature(filename);
        FileOutputStream out = new FileOutputStream(outfile.toString());
        out.write(output);
        out.flush();
        out.close();
    }

    private byte[] calculateSignature(String filename)
            throws Exception
    {
        String strippedfile = getStrippedFile(filename);
        byte[] sf = strippedfile.getBytes(java.nio.charset.Charset.forName("US-ASCII"));
        Signature sig = Signature.getInstance(SIGALGORITHM);
        sig.initSign(privateKey);
        sig.update(sf);
        return sig.sign();
    }

    private String getStrippedFile(String f)
            throws Exception
    {
        StringBuilder inbuf = new StringBuilder();
        FileInputStream in = new FileInputStream(f);
        int c = 0;
        while ((c = in.read()) != -1) {
            char chr = (char)c;
            if (!isSpaceChar(chr)) {
                inbuf.append(chr);
            }
        }
        in.close();
        return inbuf.toString();
    }

    private boolean isSpaceChar(char c) {
        if (c == '\r')
            return true;
        if (c == '\n')
            return true;
        if (c == '\t')
            return true;
        if (c == ' ')
            return true;
        return false;
    }

    public String verifySignature(String filename)
        throws Exception
    {
        if (bootException != null) {
            throw bootException;
        }
        Signature sig = Signature.getInstance(SIGALGORITHM);
        sig.initVerify(certificate);
        StringBuilder outfile = new StringBuilder(filename);
        String strippedfile = getStrippedFile(filename);
        outfile.append(SIGNATUREEXTENSION);
        File sigfile = new File(outfile.toString());
        byte[] submittedSignature = new byte[(int)sigfile.length()];
        FileInputStream in = new FileInputStream(sigfile);
        in.read(submittedSignature);
        in.close();
        sig.update(strippedfile.getBytes(java.nio.charset.Charset.forName("US-ASCII")));
        if (sig.verify(submittedSignature)) {
            return "Signature OK: " + filename;
        } else {
            return "Calculated and given signatures do not match: " + filename;
        }
    }
}
