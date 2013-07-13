package cn.lichengwu.jvm.rmi;

// Copyright MageLang Institute; Version $Id: //depot/main/src/edu/modules/RMI-mml2/magercises/DistributedGarbageCollector/Solution/MessageObjectImpl.java#2 $
import java.io.Serializable;
import java.rmi.server.*;
import java.rmi.*;

public class MessageObjectImpl extends UnicastRemoteObject implements MessageObject, Serializable,
        Unreferenced {

    private static final long serialVersionUID = -1264071649098424299L;
	static int number = 0;
	private int objNumber;

	public MessageObjectImpl() throws RemoteException {
		objNumber = number;
		System.out.println("MessageObject: Class Number is #" + number + " Object Number is #"
		        + objNumber);
		number = number + 1;
	}

	public int getNumberFromObject() {
		return objNumber;
	}

	public int getNumberFromClass() {
		return number;
	}

	public void finalize() throws Throwable {
		super.finalize();

		System.out.println("MessageObject: Finalize for object #: " + objNumber);
	}

	public void unreferenced() {
		System.out.println("MessageObject: Unreferenced for object #: " + objNumber);
	}

}
