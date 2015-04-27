package com.word;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TestMain {

	public static final String NAME = "article.txt";

	public static final String RESULT = "王成洋-result.txt";

	public static final int actorcount = 2;

	public static void main(String[] args) throws Exception {
		try {
			if (actorcount == 0)
				return;
			/*int availableProcessors = Runtime.getRuntime().availableProcessors();
			System.err.println(availableProcessors);*/

			final ActorRef mainActor = ActorSystem.create().actorOf(Props.create(MainActor.class));
			mainActor.tell("start", mainActor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
