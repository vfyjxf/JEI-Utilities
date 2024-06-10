package com.github.vfyjxf.jeiutilities.event;

public interface IEventContext {

    EventManager<?> getManager();

    @SuppressWarnings("unchecked")
    default <T> T holder() {
        return (T) getManager().holder();
    }

    boolean isCancelled();

    boolean isInterrupted();

    final class Common implements IEventContext {

        private final EventManager<?> manager;
        private boolean cancelled = false;
        private boolean interrupted = false;

        public Common(EventManager<?> manager) {
            this.manager = manager;
        }

        @Override
        public EventManager<?> getManager() {
            return manager;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
            interrupted = true;
        }

        public void interrupt() {
            interrupted = true;
        }

        @Override
        public boolean isInterrupted() {
            return interrupted;
        }
    }

    final class Cancelable implements IEventContext {

        private final EventManager<?> manager;
        private boolean cancelled = false;

        public Cancelable(EventManager<?> manager) {
            this.manager = manager;
        }

        @Override
        public EventManager<?> getManager() {
            return manager;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
        }

        @Override
        public boolean isInterrupted() {
            return cancelled;
        }

    }

    final class Interruptible implements IEventContext {

        private final EventManager<?> manager;
        private boolean interrupted = false;

        public Interruptible(EventManager<?> manager) {
            this.manager = manager;
        }

        @Override
        public EventManager<?> getManager() {
            return manager;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public void interrupt() {
            interrupted = true;
        }

        @Override
        public boolean isInterrupted() {
            return interrupted;
        }
    }
}
