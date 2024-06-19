package dev.vfyjxf.jeiutilities.api.event;

public interface IEventContext {

    EventChannel<?> getChannel();

    @SuppressWarnings("unchecked")
    default <T> T holder() {
        return (T) getChannel().holder();
    }

    boolean isCancelled();

    boolean isInterrupted();

    final class Common implements IEventContext {

        private final EventChannel<?> manager;
        private boolean cancelled = false;
        private boolean interrupted = false;

        public Common(EventChannel<?> manager) {
            this.manager = manager;
        }

        @Override
        public EventChannel<?> getChannel() {
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

        private final EventChannel<?> manager;
        private boolean cancelled = false;

        public Cancelable(EventChannel<?> manager) {
            this.manager = manager;
        }

        @Override
        public EventChannel<?> getChannel() {
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

        private final EventChannel<?> manager;
        private boolean interrupted = false;

        public Interruptible(EventChannel<?> manager) {
            this.manager = manager;
        }

        @Override
        public EventChannel<?> getChannel() {
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
