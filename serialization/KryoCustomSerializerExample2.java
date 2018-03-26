
public class KryoCustomSerializerExample2 {

    private final Serializer SERIALIZER = Serializer.using(KryoNamespace.newBuilder()
        .register(new HeartbeatMessageSerializer(), HeartbeatMessage.class)
        .register(ControllerNode.class)
        .register(ControllerNode.State.class)
        .register(NodeId.class)
        .build());

    private static class HeartbeatMessageSerializer extends com.esotericsoftware.kryo.Serializer<HeartbeatMessage> {
        private final byte VERSION = 2;

        @Override
        public void write(Kryo kryo, Output output, HeartbeatMessage message) {
            output.writeByte(VERSION);
            kryo.writeObject(output, message.source());
            kryo.writeObject(output, message.state());
            output.writeLong(message.timestamp());
        }

        @Override
        public HeartbeatMessage read(Kryo kryo, Input input, Class<HeartbeatMessage> type) {
            byte version = input.readByte();
            ControllerNode source = kryo.readObject(input, ControllerNode.class);
            ControllerNode.State state = kryo.readObject(input, ControllerNode.State.class);
            switch (version) {
                case 1:
                    return new HeartbeatMessage(source, state, System.currentTimeMillis());
                case 2:
                    long timestamp = input.readLong();
                    return new HeartbeatMessage(source, state, timestamp);
                default:
                    throw new AssertionError();
            }
        }
    }

    private static class HeartbeatMessage {
        private ControllerNode source;
        private State state;
        private long timestamp;

        public HeartbeatMessage(ControllerNode source, State state, long timestamp) {
            this.source = source;
            this.state = state != null ? state : State.ACTIVE;
            this.timestamp = timestamp;
        }

        public ControllerNode source() {
            return source;
        }

        public State state() {
            return state;
        }

        public long timestamp() {
            return timestamp;
        }
    }
}
