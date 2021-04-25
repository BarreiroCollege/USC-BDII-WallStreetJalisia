package gal.sdc.usc.wallstreet.util;

public interface Comunicador {
    default Object[] getData() {
        return null;
    }

    default void onSuccess() {

    }

    default void onFailure() {

    }
}
