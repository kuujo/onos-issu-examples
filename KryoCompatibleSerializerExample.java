
public class KryoCompatibleSerializerExample {

    private final Serializer SERIALIZER = Serializer.using(KryoNamespace.newBuilder()
        .setCompatible(true)
        .register(HeartbeatMessage.class)
        .register(ControllerNode.class)
        .register(ControllerNode.State.class)
        .register(NodeId.class)
        .build());

    private static class HeartbeatMessageSerializer extends com.esotericsoftware.kryo.Serializer<HeartbeatMessage> {
        private final byte VERSION = 1;

        @Override
        public void write(Kryo kryo, Output output, HeartbeatMessage message) {
            output.writeByte(VERSION);
            kryo.writeObject(output, message.source());
            kryo.writeObject(output, message.state());
        }

        @Override
        public HeartbeatMessage read(Kryo kryo, Input input, Class<HeartbeatMessage> type) {
            byte version = input.readByte();
            ControllerNode source = kryo.readObject(input, ControllerNode.class);
            ControllerNode.State state = kryo.readObject(input, ControllerNode.State.class);
            return new HeartbeatMessage(source, state);
        }
    }

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
