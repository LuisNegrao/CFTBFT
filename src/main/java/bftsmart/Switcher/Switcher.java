package bftsmart.Switcher;

import bftsmart.Switcher.Messages.ConfirmationMessage;
import bftsmart.Switcher.Messages.SwitchMessage;
import bftsmart.Switcher.Messages.TriggerMessage;
import bftsmart.communication.SystemMessage;
import bftsmart.consensus.roles.Acceptor;
import bftsmart.tom.core.TOMLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;

public class Switcher extends Thread {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Acceptor acceptor;
    private TOMLayer tomLayer;
    private List<SystemMessage> confirmationReceived;
    private List<SystemMessage> switchReceived;
    private boolean switchExecuted;



    public Switcher(Acceptor acceptor, TOMLayer tomLayer) {

        this.acceptor = acceptor;
        this.tomLayer = tomLayer;
        this.confirmationReceived = new ArrayList<>();
        this.switchReceived = new ArrayList<>();
        this.switchExecuted = false;
    }


    private void sendMessage(SystemMessage message) {
        int[] targets = this.tomLayer.controller.getCurrentViewAcceptors();
        this.tomLayer.getCommunication().getServersConn().send(targets, message, true);
    }

    // find a way to return the size of messages with the same epoch
    private int count(List<SystemMessage> list) {
        return list.size();
    }


    /**
     * This function is ment to handle the arrival of a triggerMessage.
     *
     * @param message message that has arrived.
     */
    public void receiveTriggerMessage(TriggerMessage message) {

        // Check if the algorithm is already running.
        if (this.tomLayer.controller.getStaticConf().running.get() && message.getSender() != this.tomLayer.controller.getStaticConf().getProcessId()) {
            logger.info("Trigger message discarded since the protocol is already running");
            return;
        }

        if (!tomLayer.controller.getStaticConf().canChange(message.getId())) {
            logger.info("Trigger message discarded since the protocol can not run at this moment");
            return;
        }

        this.tomLayer.controller.getStaticConf().running.set(true);
        logger.info("Trigger Process started by: " + message.getSender() + " with id: " + message.getId());

        int epoch = this.acceptor.getExecutionManager().getConsensus(message.getId()).
                getEpoch(message.getEpoch(), this.tomLayer.controller).getTimestamp();

        SwitchMessage switchMessages = new SwitchMessage(message.getId(), epoch, this.tomLayer.controller.getStaticConf().getProcessId());

        sendMessage(switchMessages);

    }

    public void receiveSwitchMessage(SwitchMessage message) {

        if (!this.tomLayer.controller.getStaticConf().running.get()) {
            logger.info("Discarded Switch message since the protocol is not running.");
            return;
        }

        if (this.switchExecuted) {
            logger.info("Discarded Switch message since the switch phase has already finished.");
            return;
        }

        this.switchReceived.add(message);

        if (count(this.switchReceived) > this.tomLayer.controller.getQuorumBFT()) {

            logger.info("Got " + count(this.switchReceived) + " switch messages. The protocol can proceed");

            if (this.tomLayer.controller.getStaticConf().isBFT()) {
                this.tomLayer.controller.getStaticConf().setBFT(false);
            } else {
                this.tomLayer.controller.getStaticConf().setBFT(true);
            }

            this.switchExecuted = true;
            logger.info("Switch phase has ended. BFT state: " + this.tomLayer.controller.getStaticConf().isBFT());

            int epoch = this.acceptor.getExecutionManager().getConsensus(message.getId()).
                    getEpoch(message.getEpoch(), this.tomLayer.controller).getTimestamp();

            ConfirmationMessage confirmationMessage = new ConfirmationMessage(message.getId(), epoch,
                    this.tomLayer.controller.getStaticConf().getProcessId());

            sendMessage(confirmationMessage);


        }
    }

    public void receiveConfirmation(ConfirmationMessage message) {

        if (!this.tomLayer.controller.getStaticConf().running.get()) {
            logger.info("Discarded Confirmation message since the protocol is not running");
        }

        this.confirmationReceived.add(message);

        if (count(this.confirmationReceived) > this.tomLayer.controller.getQuorumBFT()) {
            this.confirmationReceived.clear();
            this.switchReceived.clear();
            this.tomLayer.controller.getStaticConf().running.set(false);
            this.tomLayer.controller.getStaticConf().setLastChange(message.getId());
            this.switchExecuted = false;
            logger.info("Change protocol has ended. Normal execution will begin");
        }

    }

    @Override
    public void run() {
        logger.info("Inside default thread");
        while (!Thread.currentThread().isInterrupted()) {


        }

    }


}
