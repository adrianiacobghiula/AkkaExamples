package aig.examples.akka;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.function.Function;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.typed.javadsl.ActorSink;

import java.util.Arrays;

public class ActorSinkExampleTest {
  public static void main(String[] args) throws InterruptedException {
    ActorSystem<Void> mySystem = ActorSystem.create(setup(), "mySystem");

    Thread.sleep(1000);

    System.out.println(mySystem.printTree());
    mySystem.terminate();

  }

  private static Behavior<Void> setup() {
    return Behaviors.setup((Function<ActorContext<Void>, Behavior<Void>>) context -> {

      ActorRef<ActorSinkExample.Command> simpleActorSink = context.spawn(ActorSinkExample.create(), "ActorSinkActorRef");

      Sink<ActorSinkExample.Command, NotUsed> commandNotUsedSink = ActorSink.actorRef(simpleActorSink, new ActorSinkExample.CompleteCommand(), ActorSinkExample.ThrowableCommand::new);

      RunnableGraph<NotUsed> pairRunnableGraph = Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
          .map(t -> t + 2)
          .map(ActorSinkExample.MessageCommand::new)
          .map(t -> (ActorSinkExample.Command) t)
          .to(commandNotUsedSink);
      NotUsed run = pairRunnableGraph.run(context.getSystem());

      return Behaviors.empty();
    });
  }
}
