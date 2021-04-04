package com.wssecurity.server;

import javax.jws.WebService;


@WebService(endpointInterface = "app.biblioteca.service.BibliotecaWS")
public class BibliotecaWSImpl implements BibliotecaWS {

	@Override
	public void hello(String nome) {
         System.out.println("akakak: "+nome);
	}

}
