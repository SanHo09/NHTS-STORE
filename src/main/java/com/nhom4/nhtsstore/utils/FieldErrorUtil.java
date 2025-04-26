package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.common.FieldValidationError;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class FieldErrorUtil {

    private static final String ERROR_ICON_PATH = "/icons/TdesignErrorCircleFilled.svg";
    private static final Color ERROR_COLOR = Color.decode("#ef0b0b");
    private static final Map<MFXTextField, MFXTooltip> activeTooltips = new HashMap<>();

    public static  <T> void showErrorTooltip( List<FieldValidationError> errors, Map<String, MFXTextField> fieldMap) {
        clearErrors();
        for (FieldValidationError error : errors) {
            MFXTextField field = fieldMap.get(error.getField());
            if (field != null) {
                MFXTooltip tooltip = createTooltip(field, error.getMessage());
                activeTooltips.put(field, tooltip);

                tooltip.show(field, Alignment.of(HPos.RIGHT, VPos.TOP), 0, -field.getPrefHeight());
            }
        }

    }

    public static void clearErrors() {
        activeTooltips.values().forEach(MFXTooltip::hide);
        activeTooltips.clear();
    }

    private static MFXTooltip createTooltip(MFXTextField field, String text) {
        MFXTooltip tooltip = new MFXTooltip(field);
        tooltip.setContent(field);
        tooltip.setText(text);
        tooltip.setIcon(IconUtil.createFxImageViewFromSvg(
                ERROR_ICON_PATH, 24, 24, c -> ERROR_COLOR));
        return tooltip;
    }
}

