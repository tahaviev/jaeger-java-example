import io.jaegertracing.Configuration;
import java.util.concurrent.TimeUnit;

public final class Main {

    public static void main(final String... args) throws Exception {
        try (
            var tracer = new Configuration("myService")
                .withReporter(
                    new Configuration.ReporterConfiguration()
                        .withSender(
                            new Configuration.SenderConfiguration()
                                .withAgentHost("tracing")
                        )
                )
                .withSampler(
                    new Configuration.SamplerConfiguration()
                        .withParam(1)
                )
                .getTracer()
        ) {
            while (true) {
                final var span = tracer.buildSpan("mySpan").start();
                TimeUnit.SECONDS.sleep(1L);
                try (var scope = tracer.activateSpan(span)) {
                    for (var i = 0; i < 10; i++) {
                        final var nestedSpan = tracer.buildSpan("myNestedSpan").start();
                        TimeUnit.SECONDS.sleep(1L);
                        nestedSpan.finish();
                    }
                }
                span.finish();
            }
        }
    }
}
