package com.example.concurrent.util.eda.async;

import com.example.concurrent.util.eda.Event;

import java.util.concurrent.TimeUnit;

public class AsyncEventDrivenTest {
    static class InputEvent extends Event {
        private final int x, y;

        public InputEvent(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    static class ResultEvent extends Event {
        private final int result;

        public ResultEvent(int result) {
            this.result = result;
        }

        public int getResult() {
            return result;
        }
    }

    static class AsyncResultEventHandler extends AsyncChannel<ResultEvent> {
        @Override
        public void handle(ResultEvent message) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("The result is : " + message.getResult());
        }
    }

    static class AsyncInputEventHandler extends AsyncChannel<InputEvent> {
        private final AsyncEventDispatcher dispatcher;

        public AsyncInputEventHandler(AsyncEventDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public void handle(InputEvent message) {
            System.out.printf("X: %d, Y: %d\n", message.getX(), message.getY());

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int result = message.getX() + message.getY();

            dispatcher.dispatch(new ResultEvent(result));
        }
    }

    public static void main(String[] args) {
        AsyncEventDispatcher dispatcher = new AsyncEventDispatcher();
        dispatcher.registerChannel(InputEvent.class, new AsyncInputEventHandler(dispatcher));
        dispatcher.registerChannel(ResultEvent.class, new AsyncResultEventHandler());

        // handler/channel 都是提交到线程池中执行，是异步的
        dispatcher.dispatch(new InputEvent(1, 2));
        dispatcher.dispatch(new ResultEvent(1000));

        System.out.println("main end");
    }
}
