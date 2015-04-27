package com.word;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MainActor extends UntypedActor {

	public static List<WordEntity> list = new ArrayList<WordEntity>();
	public static long startTime = 0;
	public static int[] index = new int[TestMain.actorcount - 1];
	public static byte[] data = null;
	static ActorRef actorOne = null;

	@Override
	public void preStart() throws Exception {
		
		MainActor.startTime = System.currentTimeMillis();
		data = Word.re();
		int size = data.length;
		int i = size % TestMain.actorcount;
		for (int j = 0; j < index.length; j++)
			index[j] = (j + 1) * (size - i) / TestMain.actorcount;
	}

	@SuppressWarnings("unused")
	@Override
	public void onReceive(Object message) throws Exception {
		if (message.equals("end")) {
			if (actorOne != null)
				getContext().stop(actorOne);
			getContext().stop(getSelf());
		} else if (message.equals("start")) {
			List<Integer> indexList = null;
			for (int i = 0; i < TestMain.actorcount; i++) {
				indexList = new ArrayList<Integer>();
				actorOne = ActorSystem.create().actorOf(Props.create(ActorOne.class), "actor" + i);
				if (TestMain.actorcount == 1) {
					indexList.add(0);
					indexList.add(data.length);
					indexList.add(0);
				} else if (i == TestMain.actorcount - 1) {
					indexList.add(index[i - 1]);
					indexList.add(data.length);
					indexList.add(1);
				} else if (i == 0) {
					indexList.add(0);
					indexList.add(index[i]);
					indexList.add(0);
				} else {
					indexList.add(index[i - 1]);
					indexList.add(index[i]);
					indexList.add(1);
				}

				actorOne.tell(indexList, getSelf());
			}
		} else {
			list.add((WordEntity) message);
			if (list.size() == TestMain.actorcount) {
				Word.last(TestMain.actorcount);
				getSelf().tell("end", getSelf());
			}
			unhandled(message);
		}
	}

}
