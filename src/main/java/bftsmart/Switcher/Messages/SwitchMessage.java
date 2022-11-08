package bftsmart.Switcher.Messages;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.Epoch;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SwitchMessage extends SystemMessage {

    private int id;
    private Epoch epochObj;
    private int epoch;
    private Object proof;

    public SwitchMessage() {}

    public SwitchMessage(int id, int epoch, int from) {

        super(from);
        this.id = id;
        this.epoch = epoch;
        this.proof = generateProof();

    }

    private Object generateProof() {
        return null;
    }

    public int getId() {
        return id;
    }

    public Epoch getEpochObj() {
        return epochObj;
    }

    public int getEpoch() {
        return epoch;
    }

    public Object getProof() {
        return proof;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(id);
        out.writeInt(epoch);
        out.writeObject(proof);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.id = in.readInt();
        this.epoch = in.readInt();
        this.proof = in.readObject();
    }
}
