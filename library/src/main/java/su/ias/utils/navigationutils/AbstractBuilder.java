package su.ias.utils.navigationutils;

import android.support.v4.app.DialogFragment;

import java.io.Serializable;

/**
 * Created on 6/5/17.
 */

abstract class AbstractBuilder<T extends AbstractBuilder> implements Serializable {

    private Double fromLatitude = null;
    private Double fromLongitude = null;
    private double toLatitude;
    private double toLongitude;
    private boolean useSave = true;
    private String title = null;
    private String saveTitle = null;

    AbstractBuilder(double toLatitude, double toLongitude) {
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
    }

    abstract DialogFragment build();

    Double getFromLatitude() {
        return fromLatitude;
    }

    T setFromLatitude(Double fromLatitude) {
        this.fromLatitude = fromLatitude;
        return (T) this;
    }

    Double getFromLongitude() {
        return fromLongitude;
    }

    T setFromLongitude(Double fromLongitude) {
        this.fromLongitude = fromLongitude;
        return (T) this;
    }

    double getToLatitude() {
        return toLatitude;
    }

    double getToLongitude() {
        return toLongitude;
    }

    boolean isUseSave() {
        return useSave;
    }

    T setUseSave(boolean useSave) {
        this.useSave = useSave;
        return (T) this;
    }

    String getTitle() {
        return title;
    }

    T setTitle(String title) {
        this.title = title;
        return (T) this;
    }

    String getSaveTitle() {
        return saveTitle;
    }

    T setSaveTitle(String saveTitle) {
        this.saveTitle = saveTitle;
        return (T) this;
    }
}
