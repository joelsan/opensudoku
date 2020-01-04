/*
 * Copyright (C) 2009 Roman Masek
 *
 * This file is part of OpenSudoku.
 *
 * OpenSudoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSudoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenSudoku.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.moire.opensudoku.gui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.TextView;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.utils.AndroidUtils;

/**
 * A {@link Preference} that allows for setting and previewing a Sudoku Board theme.
 */
public class SudokuBoardThemePreference extends ListPreference {
    /**
     * The edit text shown in the dialog.
     */
    private SudokuBoardView mBoard;
    private int mClickedDialogEntryIndex;

    public SudokuBoardThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SudokuBoardThemePreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        mClickedDialogEntryIndex = findIndexOfValue(getValue());
        builder.setSingleChoiceItems(getEntries(), mClickedDialogEntryIndex,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mClickedDialogEntryIndex = which;
                        SudokuBoardThemePreference.this.applyThemePreview(
                                getEntryValues()[mClickedDialogEntryIndex].toString());
                    }
                });

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View sudokuPreviewView = inflater.inflate(R.layout.preference_dialog_sudoku_board_theme, null);
        prepareSudokuPreviewView(sudokuPreviewView, getValue());
        builder.setCustomTitle(sudokuPreviewView);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && mClickedDialogEntryIndex >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    private void prepareSudokuPreviewView(View view, String initialTheme) {
        mBoard = (SudokuBoardView) view.findViewById(R.id.sudoku_board);
        mBoard.setFocusable(false);

        CellCollection cells = CellCollection.createDebugGame();
        cells.getCell(0, 0).setValue(1);
        cells.fillInNotes();
        mBoard.setCells(cells);

        applyThemePreview(initialTheme);
    }

    private void applyThemePreview(String theme) {

        if (theme.equals("custom")) {

            SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(getContext());
            mBoard.setLineColor(gameSettings.getInt("custom_theme_lineColor", R.color.default_lineColor));
            mBoard.setSectorLineColor(gameSettings.getInt("custom_theme_sectorLineColor", R.color.default_sectorLineColor));
            mBoard.setTextColor(gameSettings.getInt("custom_theme_textColor", R.color.default_textColor));
            mBoard.setTextColorReadOnly(gameSettings.getInt("custom_theme_textColorReadOnly", R.color.default_textColorReadOnly));
            mBoard.setTextColorNote(gameSettings.getInt("custom_theme_textColorNote", R.color.default_textColorNote));
            mBoard.setBackgroundColor(gameSettings.getInt("custom_theme_backgroundColor", R.color.default_backgroundColor));
            mBoard.setBackgroundColorSecondary(gameSettings.getInt("custom_theme_backgroundColorSecondary", R.color.default_backgroundColorSecondary));
            mBoard.setBackgroundColorReadOnly(gameSettings.getInt("custom_theme_backgroundColorReadOnly", R.color.default_backgroundColorReadOnly));
            mBoard.setBackgroundColorTouched(gameSettings.getInt("custom_theme_backgroundColorTouched", R.color.default_backgroundColorTouched));
            mBoard.setBackgroundColorSelected(gameSettings.getInt("custom_theme_backgroundColorSelected", R.color.default_backgroundColorSelected));
            mBoard.setBackgroundColorHighlighted(gameSettings.getInt("custom_theme_backgroundColorHighlighted", R.color.default_backgroundColorHighlighted));
        } else {
            ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getContext(), AndroidUtils.getThemeResourceIdFromString(theme));

            int[] attributes = {
                    R.attr.lineColor,
                    R.attr.sectorLineColor,
                    R.attr.textColor,
                    R.attr.textColorReadOnly,
                    R.attr.textColorNote,
                    R.attr.backgroundColor,
                    R.attr.backgroundColorSecondary,
                    R.attr.backgroundColorReadOnly,
                    R.attr.backgroundColorTouched,
                    R.attr.backgroundColorSelected,
                    R.attr.backgroundColorHighlighted
            };

            TypedArray themeColors = themeWrapper.getTheme().obtainStyledAttributes(attributes);
            mBoard.setLineColor(themeColors.getColor(0, R.color.default_lineColor));
            mBoard.setSectorLineColor(themeColors.getColor(1, R.color.default_sectorLineColor));
            mBoard.setTextColor(themeColors.getColor(2, R.color.default_textColor));
            mBoard.setTextColorReadOnly(themeColors.getColor(3, R.color.default_textColorReadOnly));
            mBoard.setTextColorNote(themeColors.getColor(4, R.color.default_textColorNote));
            mBoard.setBackgroundColor(themeColors.getColor(5, R.color.default_backgroundColor));
            mBoard.setBackgroundColorSecondary(themeColors.getColor(6, R.color.default_backgroundColorSecondary));
            mBoard.setBackgroundColorReadOnly(themeColors.getColor(7, R.color.default_backgroundColorReadOnly));
            mBoard.setBackgroundColorTouched(themeColors.getColor(8, R.color.default_backgroundColorTouched));
            mBoard.setBackgroundColorSelected(themeColors.getColor(9, R.color.default_backgroundColorSelected));
            mBoard.setBackgroundColorHighlighted(themeColors.getColor(10, R.color.default_backgroundColorHighlighted));
        }
        mBoard.invalidate();
    }
}