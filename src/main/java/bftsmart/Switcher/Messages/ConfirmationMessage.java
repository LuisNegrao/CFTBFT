package bftsmart.Switcher.Messages;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.Epoch;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ConfirmationMessage extends SystemMessage {

    private int id;
    private Epoch epochObj;
    private int epoch;
    private Object proof;

    public ConfirmationMessage() {

    }

    public ConfirmationMessage(int id, int epoch, int from) {
        super(from);
        this.id = id;
        this.epoch = epoch;
        this.epochObj = null;
        this.proof = null;

    }

    public int getId() {
        return this.id;
    }

    public int egetEoch() {
        return this.epoch;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(id);
        out.writeInt(epoch);
        out.writeObject(proof);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        super.readExternal(in);
        this.id = in.readInt();
        this.epoch = in.readInt();
        this.proof = in.readObject();

    }
}
