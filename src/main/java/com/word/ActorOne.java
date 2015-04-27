package com.word;

import java.util.List;

import akka.actor.UntypedActor;

public class ActorOne extends UntypedActor {

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object message) throws Exception {
		List<Integer> list = (List<Integer>) message;
		WordEntity wordEntity = new WordEntity(MainActor.data, list.get(0), list.get(1), list.get(2));
		getSender().tell(wordEntity, getSelf());
	}

}
