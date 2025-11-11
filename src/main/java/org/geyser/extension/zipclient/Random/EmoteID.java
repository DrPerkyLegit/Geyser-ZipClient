package org.geyser.extension.zipclient.Random;

import java.util.Objects;

public enum EmoteID {
    Follow("17428c4c-3813-4ea1-b3a9-d6a32f83afca"),
    Waving("4c8ae710-df2e-47cd-814d-cc7bf21a3d67");

    public final String ID;

    EmoteID(String ID) {
        this.ID = ID;
    }

    public boolean isSame(String ID) {
        return Objects.equals(this.ID, ID);
    }
}
