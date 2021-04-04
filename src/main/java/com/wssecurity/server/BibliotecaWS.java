package com.wssecurity.server;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.DOCUMENT)
@HandlerChain(file = "handler-chain.xml")
public interface BibliotecaWS {

	public void hello(String nome);

}
