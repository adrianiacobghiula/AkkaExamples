package aig.examples.akka;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.function.Function;
import akka.stream.javadsl.*;
import akka.stream.typed.javadsl.ActorSink;

import java.util.Arrays;

public class ActorSinkWithBackpressureTest {
  public static void main(String[] args) throws InterruptedException {
    ActorSystem<Void> mySystem = ActorSystem.create(setup(), "mySystem");
    Thread.sleep(1000);

    System.out.println(mySystem.printTree());
    mySystem.terminate();
  }

  private static Behavior<Void> setup() {
    return Behaviors.setup((Function<ActorContext<Void>, Behavior<Void>>) context -> {

      ActorRef<ActorSinkWithBackpressure.Command> simpleActorSink = context.spawn(ActorSinkWithBackpressure.create(), "ActorSinkWithBackpressure");

      Sink<Integer, NotUsed> ack =
          ActorSink.actorRefWithBackpressure(simpleActorSink,
              ActorSinkWithBackpressure.MessageCommand::new,
              ActorSinkWithBackpressure.OnInitCommand::new,
              ActorSinkWithBackpressure.Ack.INSTANCE,
              ActorSinkWithBackpressure.CompleteCommand.INSTANCE,
              ActorSinkWithBackpressure.ThrowableCommand::new);


      RunnableGraph<NotUsed> pairRunnableGraph = Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
          .map(t -> t + 2)
          .to(ack);

      NotUsed run = pairRunnableGraph.run(context.getSystem());

      return Behaviors.empty();
    });
  }
}
