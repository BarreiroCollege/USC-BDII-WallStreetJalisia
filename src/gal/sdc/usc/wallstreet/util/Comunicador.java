package gal.sdc.usc.wallstreet.util;

public interface Comunicador {
    default Object[] getData() {
        return new Object[0];
    }

    default void onSuccess() {

    }

    default void onFailure() {

    }
}
