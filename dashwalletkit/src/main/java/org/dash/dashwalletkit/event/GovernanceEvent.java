package org.dash.dashwalletkit.event;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.governance.GovernanceObject;

public class GovernanceEvent {

    private Sha256Hash hash;
    private GovernanceObject governanceObject;

    public GovernanceEvent(Sha256Hash hash, GovernanceObject governanceObject) {
        this.hash = hash;
        this.governanceObject = governanceObject;
    }

    public Sha256Hash getHash() {
        return hash;
    }

    public GovernanceObject getGovernanceObject() {
        return governanceObject;
    }
}
