package nl.xillio.migrationtool.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableView;

/**
 * Utility class to make table columns autosize to a certain percentage instead of a fixed width.
 *
 * @author Xillio
 */
public class PTableColumn<S, T> extends javafx.scene.control.TableColumn<S, T> {

    private final DoubleProperty percentageWidth = new SimpleDoubleProperty(1);

    public PTableColumn() {
        tableViewProperty().addListener((ChangeListener<TableView<S>>) (ov, t, t1) -> {
            if (PTableColumn.this.prefWidthProperty().isBound()) {
                PTableColumn.this.prefWidthProperty().unbind();
            }

            PTableColumn.this.prefWidthProperty().bind(t1.widthProperty().multiply(percentageWidth));
        });
    }

    public final DoubleProperty percentageWidthProperty() {
        return this.percentageWidth;
    }

    public final double getPercentageWidth() {
        return this.percentageWidthProperty().get();
    }

    public final void setPercentageWidth(final double value) throws IllegalArgumentException {
        if (value >= 0 && value <= 1) {
            this.percentageWidthProperty().set(value);
        } else {
            throw new IllegalArgumentException(String.format("The provided percentage width is not between 0.0 and 1.0. Value is: %1$s", value));
        }
    }
}
