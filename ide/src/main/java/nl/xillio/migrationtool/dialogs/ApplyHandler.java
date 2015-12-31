package nl.xillio.migrationtool.dialogs;

/**
 * This interface is a blueprint for applying the updated settings
 * <p>
 * Created by Pieter Soels on 19/11/2015.
 */

@FunctionalInterface
public interface ApplyHandler {
    void applySettings();
}