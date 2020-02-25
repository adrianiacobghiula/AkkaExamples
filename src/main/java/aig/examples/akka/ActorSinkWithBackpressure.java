package aig.examples.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ActorSinkWithBackpressure extends AbstractBehavior<ActorSinkWithBackpressure.Command> {
  public interface Command {
  }

  public static class OnInitCommand implements Command {
    private final ActorRef<Ack> ackTo;

    public OnInitCommand(ActorRef<Ack> ackTo) {
      this.ackTo = ackTo;
    }

    public ActorRef<Ack> getAckTo() {
      return ackTo;
    }
  }

  public enum Ack implements Command {
    INSTANCE
  }


  public static class MessageCommand implements Command {
    private ActorRef<Ack> ackTo;
    private final Integer msg;

    public MessageCommand(ActorRef<Ack> ackTo, Integer msg) {
      this.ackTo = ackTo;
      this.msg = msg;
    }

    public ActorRef<Ack> getAckTo() {
      return ackTo;
    }

    public Integer getMsg() {
      return msg;
    }
  }


  public enum CompleteCommand implements Command {
    INSTANCE
  }

  public static class ThrowableCommand implements Command {
    private final Throwable ex;

    public ThrowableCommand(Throwable ex) {
      this.ex = ex;
    }

    public Throwable getEx() {
      return ex;
    }
  }

  public static Behavior<ActorSinkWithBackpressure.Command> create() {
    return Behaviors.setup(ActorSinkWithBackpressure::new);
  }

  public ActorSinkWithBackpressure(ActorContext<Command> context) {
    super(context);
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(OnInitCommand.class, this::onInit)
        .onMessage(MessageCommand.class, this::onMessage)
        .onMessage(ThrowableCommand.class, this::onThrowable)
        .onMessage(CompleteCommand.class, this::onComplete)
        .build();
  }

  private Behavior<Command> onInit(OnInitCommand m) {
    System.out.println("onInit()");
    m.ackTo.tell(Ack.INSTANCE);
    return Behaviors.same();
  }

  private Behavior<Command> onMessage(MessageCommand m) {
    System.out.println("onMessage(" + m.getMsg() + ")");
    m.ackTo.tell(Ack.INSTANCE);
    return Behaviors.same();
  }

  private Behavior<Command> onThrowable(ThrowableCommand m) {
    System.out.println("onThrowable(" + m.getEx().getMessage() + ")");
    return Behaviors.same();
  }

  private Behavior<Command> onComplete(CompleteCommand m) {
    System.out.println(getContext().getSystem().printTree());
    System.out.println("onComplete()");
    return Behaviors.stopped();
  }

}
