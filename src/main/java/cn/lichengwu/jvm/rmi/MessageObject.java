package cn.lichengwu.jvm.rmi;

// Copyright MageLang Institute; Version $Id: //depot/main/src/edu/modules/RMI-mml2/magercises/DistributedGarbageCollector/Solution/MessageObject.java#2 $

public

interface MessageObject extends java.rmi.Remote

{

   public int getNumberFromObject() throws java.rmi.RemoteException;

   public int getNumberFromClass() throws java.rmi.RemoteException;

}
