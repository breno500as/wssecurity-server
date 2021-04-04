package com.wssecurity.server;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;

public class BibliotecaHandler implements SOAPHandler<SOAPMessageContext>{


    private XWSSProcessor processor = null;

    public BibliotecaHandler() {

    	try {

    		XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
    		processor = factory.createProcessorForSecurityConfiguration(getClass().getResourceAsStream("/xwss-config.xml"), new BibliotecaCertificateCallback());
    		//processor = factory.createProcessorForSecurityConfiguration(getClass().getResourceAsStream("/encrypt-xwss-config.xml"), new BibliotecaCertificateCallback());


    	} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



	@Override
	public boolean handleMessage(SOAPMessageContext context) {

		Boolean out = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		SOAPMessage message = context.getMessage();

		//Verifica as credenciais na chegada da requisi�ao no web service
		if(!out){

			try {

				ProcessingContext processingContex = processor.createProcessingContext(message);
				processingContex.setSOAPMessage(message);

				SOAPMessage verifiedMessage = processor.verifyInboundMessage(processingContex);
				context.setMessage(verifiedMessage);

				System.out.println(SubjectAccessor.getRequesterSubject(processingContex));

				System.out.println("SOAP Message chegando no servidor: ");
				verifiedMessage.writeTo(System.out);


			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
		QName hdr = new QName(uri,"Security","wsse");
		HashSet<QName> headers = new HashSet<QName>();
		headers.add(hdr);
		return headers;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) { return true; }

	@Override
	public void close(MessageContext context) { }


}
