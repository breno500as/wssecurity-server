package com.wssecurity.server;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.sun.xml.wss.impl.callback.CertificateValidationCallback;
import com.sun.xml.wss.impl.callback.DecryptionKeyCallback;
import com.sun.xml.wss.impl.callback.TimestampValidationCallback;
import com.sun.xml.wss.impl.callback.CertificateValidationCallback.CertificateValidationException;
import com.sun.xml.wss.impl.callback.DecryptionKeyCallback.X509CertificateBasedRequest;
import com.sun.xml.wss.impl.callback.TimestampValidationCallback.TimestampValidationException;

public class BibliotecaCertificateCallback implements CallbackHandler {



	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			Callback callback = callbacks[i];

			//Callback que requere uma mensagem assinada xwss-config.xml
			if (callback instanceof CertificateValidationCallback) {

				CertificateValidationCallback cb = (CertificateValidationCallback) callback;
				cb.setValidator(new CertificateValidator());

		     //Callback que requere uma mensagem criptografada por um certificado encrypt-xwss-config.xml
			}else if(callback instanceof DecryptionKeyCallback){
				DecryptionKeyCallback cb = (DecryptionKeyCallback) callback;

				X509CertificateBasedRequest x509 = (X509CertificateBasedRequest) cb.getRequest();

				try {

					PrivateKey privKey = (PrivateKey) getKeyStore().getKey("breno", "123456".toCharArray());
					x509.setPrivateKey(privKey);
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}


			} else if (callback instanceof TimestampValidationCallback) {

				TimestampValidationCallback cb = (TimestampValidationCallback) callback;
				cb.setValidator(new TimeValidator());

			}

		}

	}

	private class TimeValidator implements TimestampValidationCallback.TimestampValidator {

		@Override
		public void validate(com.sun.xml.wss.impl.callback.TimestampValidationCallback.Request arg0)
				throws TimestampValidationException {

		}

	}

	private class CertificateValidator implements CertificateValidationCallback.CertificateValidator {

		@Override
		public boolean validate(X509Certificate certificado) throws CertificateValidationException {


			try {



				X509Certificate certificatePerson = (X509Certificate) getKeyStore().getCertificate("breno");
				System.out.println(certificado.getIssuerX500Principal().getName());

				//Verifica se o certificado foi assinado com a private key correspondente a public key especifificada
				certificado.verify(certificatePerson.getPublicKey());
				//certificado.checkValidity();


			} catch (Exception e) {
				throw new CertificateValidationException(e);
			}

			return true;
		}

	}

	public KeyStore getKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException  {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(getClass().getResourceAsStream("/ws-security.jks"), "123456".toCharArray());
		return keyStore;
	}

}
