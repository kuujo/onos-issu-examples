
public class KryoCompatibleSerializerExample {

    private final Serializer SERIALIZER = Serializer.using(KryoNamespace.newBuilder()
        .setCompatible(true)
        .register(HeartbeatMessage.class)
        .register(ControllerNode.class)
        .register(ControllerNode.State.class)
        .register(NodeId.class)
        .build());

    private static class HeartbeatMessage {
        private ControllerNode source;
        private State state;

        public HeartbeatMessage(ControllerNode source, State state) {
            this.source = source;
            this.state = state != null ? state : State.ACTIVE;
        }

        public ControllerNode source() {
            return source;
        }

        public State state() {
            return state;
        }
    }
}
