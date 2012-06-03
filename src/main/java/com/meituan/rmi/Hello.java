package com.meituan.rmi;

// Copyright MageLang Institute; Version $Id: //depot/main/src/edu/modules/RMI-mml2/magercises/DistributedGarbageCollector/Solution/Hello.java#2 $
import java.rmi.*;

public interface Hello extends java.rmi.Remote
{
   public String        sayHello()         throws RemoteException;

   public MessageObject getMessageObject() throws RemoteException;

}
