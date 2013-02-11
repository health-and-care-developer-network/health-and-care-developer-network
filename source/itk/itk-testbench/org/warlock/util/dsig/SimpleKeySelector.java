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
package org.warlock.util.dsig;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import java.security.cert.X509Certificate;
import java.security.cert.X509CRL;
import java.util.Iterator;
import java.security.Key;

/**
 *
 * @author Apache
 *
 * This is extracted from the Apache javax.xml.crypto.test.KeySelectors class
 * published in the Apache Subversion repository.
 */
public class SimpleKeySelector  
        extends KeySelector
{
        private SimpleKeySelectorResult fixedKey = null;

        public void setFixedKey(Key k) { fixedKey = new SimpleKeySelectorResult(k); }

        @Override
	public KeySelectorResult select(KeyInfo keyInfo,
					KeySelector.Purpose purpose,
					AlgorithmMethod method,
					XMLCryptoContext context)
	    throws KeySelectorException {

            if (fixedKey != null) {
                return fixedKey;
            }

	    if (keyInfo == null) {
		throw new KeySelectorException("Null KeyInfo object!");
	    }
	    // search for X509Data in keyinfo
	    Iterator iter = keyInfo.getContent().iterator();
	    while (iter.hasNext()) {
		XMLStructure kiType = (XMLStructure) iter.next();
		if (kiType instanceof X509Data) {
		    X509Data xd = (X509Data) kiType;
		    Object[] entries = xd.getContent().toArray();
		    X509CRL crl = null;
		    // Looking for CRL before finding certificates
		    for (int i = 0; (i<entries.length&&crl != null); i++) {
			if (entries[i] instanceof X509CRL) {
			    crl = (X509CRL) entries[i];
			}
		    }
		    Iterator xi = xd.getContent().iterator();
		    boolean hasCRL = false;
		    while (xi.hasNext()) {
			Object o = xi.next();
			// skip non-X509Certificate entries
			if (o instanceof X509Certificate) {
			    if ((purpose != KeySelector.Purpose.VERIFY) &&
				(crl != null) &&
				crl.isRevoked((X509Certificate)o)) {
				continue;
			    } else {
				return new SimpleKeySelectorResult
				    (((X509Certificate)o).getPublicKey());
			    }
			}
		    }
		}
	    }
	    throw new KeySelectorException("No X509Certificate found!");
	}
    }

