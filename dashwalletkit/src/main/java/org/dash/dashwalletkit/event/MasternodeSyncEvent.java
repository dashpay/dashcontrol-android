package org.dash.dashwalletkit.event;

public class MasternodeSyncEvent {

    private MasternodeSyncStatus newStatus;
    private double syncStatus;

    public MasternodeSyncEvent(MasternodeSyncStatus newStatus, double syncStatus) {
        this.newStatus = newStatus;
        this.syncStatus = syncStatus;
    }

    public MasternodeSyncStatus getNewStatus() {
        return newStatus;
    }

    public double getSyncStatus() {
        return syncStatus;
    }

    public boolean isSyncComplete() {
        return (newStatus == MasternodeSyncStatus.MASTERNODE_SYNC_FINISHED);
    }

    public enum MasternodeSyncStatus {
        MASTERNODE_SYNC_FAILED,
        MASTERNODE_SYNC_INITIAL,    // sync just started, was reset recently or still in IDB
        MASTERNODE_SYNC_WAITING,    // waiting after initial to see if we can get more headers/blocks
        MASTERNODE_SYNC_LIST,
        MASTERNODE_SYNC_MNW,
        MASTERNODE_SYNC_GOVERNANCE,
        MASTERNODE_SYNC_GOVOBJ,
        MASTERNODE_SYNC_GOVOBJ_VOTE,
        MASTERNODE_SYNC_FINISHED;

        public static MasternodeSyncStatus valueOf(int value) {
            switch (value) {
                case -1:
                    return MASTERNODE_SYNC_FAILED;
                case 0:
                    return MASTERNODE_SYNC_INITIAL;
                case 1:
                    return MASTERNODE_SYNC_WAITING;
                case 2:
                    return MASTERNODE_SYNC_LIST;
                case 3:
                    return MASTERNODE_SYNC_MNW;
                case 4:
                    return MASTERNODE_SYNC_GOVERNANCE;
                case 10:
                    return MASTERNODE_SYNC_GOVOBJ;
                case 11:
                    return MASTERNODE_SYNC_GOVOBJ_VOTE;
                case 999:
                    return MASTERNODE_SYNC_FINISHED;
                default:
                    throw new IllegalArgumentException("Unsupported sync status " + value);
            }
        }
    }
}
