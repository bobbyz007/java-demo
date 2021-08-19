package com.example.util;

import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * 演示如何基于Throwable 定位调用轨迹
 */
public class ThrowableStackTrace {
    public static void main(String[] args) throws TraceRecord {
        final TraceRecord BOTTOM = new TraceRecord("Bottom") {
            private static final long serialVersionUID = 7396077602074694571L;

            // Override fillInStackTrace() so we not populate the backtrace via a native call and so leak the
            // Classloader.
            // See https://github.com/netty/netty/pull/10691

            // 放开以下注释，则不会输出调用栈
            /*@Override
            public Throwable fillInStackTrace() {
                return this;
            }*/
        };
        System.out.println(BOTTOM);

        TraceRecord record = test1();
        // 打印 record被创建的地方的调用栈信息
        System.out.println(record);
    }

    static TraceRecord test1() {
        return test2();
    }

    static TraceRecord test2() {
        return new TraceRecord("test2 hint");
    }

    private static class TraceRecord extends Throwable {
        private static final long serialVersionUID = 6065153674892850720L;
        private final String hintString;

        public TraceRecord(String hintString) {
            this.hintString = hintString;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder(2048);
            if (hintString != null) {
                buf.append("\tHint: ").append(hintString).append(NEWLINE);
            }

            // Append the stack trace.
            StackTraceElement[] array = getStackTrace();

            // Skip the first three elements.
            out: for (int i = 0; i < array.length; i++) {
                StackTraceElement element = array[i];
                // Strip the noisy stack trace elements.
                String[] exclusions = new String[0];
                for (int k = 0; k < exclusions.length; k += 2) {
                    // Suppress a warning about out of bounds access
                    // since the length of excludedMethods is always even, see addExclusions()
                    if (exclusions[k].equals(element.getClassName())
                            && exclusions[k + 1].equals(element.getMethodName())) { // lgtm[java/index-out-of-bounds]
                        continue out;
                    }
                }

                buf.append('\t');
                buf.append(element.toString());
                buf.append(NEWLINE);
            }
            return buf.toString();
        }
    }
}
