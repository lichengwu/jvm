package com.meituan.rmi;

// Copyright MageLang Institute; Version $Id: //depot/main/src/edu/modules/RMI-mml2/magercises/DistributedGarbageCollector/Solution/HelloImpl.java#2 $
import java.rmi.*;
import java.rmi.server.*;

public class HelloImpl extends UnicastRemoteObject implements Hello, Unreferenced {

    private static final long serialVersionUID = 6578198877791116511L;

	public HelloImpl() throws RemoteException {
		super();
	}

	public String sayHello() throws RemoteException {
		return "Hello!";

	}

	public MessageObject getMessageObject() throws RemoteException {
		MessageObject mo = new MessageObjectImpl();

		return mo;
	}

	public void unreferenced() {
		System.out.println("HelloImpl: Unreferenced");
	}

	public void finalize() throws Throwable {
		super.finalize();

		System.out.println("HelloImpl: Finalize called");
	}
}
