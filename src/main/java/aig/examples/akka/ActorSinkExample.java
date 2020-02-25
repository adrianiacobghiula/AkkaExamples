package aig.examples.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ActorSinkExample extends AbstractBehavior<ActorSinkExample.Command> {
  public interface Command {
  }

  public static class MessageCommand implements Command {
    private final int nr;

    public MessageCommand(int nr) {
      this.nr = nr;
    }

    public int getNr() {
      return nr;
    }
  }


  public static class CompleteCommand implements Command {
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

  public static Behavior<ActorSinkExample.Command> create() {
    return Behaviors.setup(ActorSinkExample::new);
  }

  public ActorSinkExample(ActorContext<Command> context) {
    super(context);
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(MessageCommand.class, this::onMessage)
        .onMessage(ThrowableCommand.class, this::onThrowable)
        .onMessage(CompleteCommand.class, this::onComplete)
        .build();
  }

  private Behavior<Command> onMessage(MessageCommand m) {
    getContext().getLog().info("onMessage({})", m.getNr());
    return Behaviors.same();
  }

  private Behavior<Command> onThrowable(ThrowableCommand m) {
    getContext().getLog().info("onThrowable({})", m.getEx().getMessage());
    return Behaviors.same();
  }

  private Behavior<Command> onComplete(CompleteCommand m) {
    getContext().getLog().info("onComplete()");
    getContext().getLog().info(getContext().getSystem().printTree());
    return Behaviors.stopped();
  }

}