/**
 * Written by Colin Greenwood
 * Date Sept 2010
 *
 * Copyright 2015 Blueshire Services Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.blueshireservices.schedulergrid;

import java.util.Date;
import java.util.Calendar;
import java.awt.event.*;
import java.awt.*;
import java.text.*;
import java.awt.event.ActionListener;

import javax.swing.text.DateFormatter;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpringLayout;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerModel;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JFormattedTextField;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.JXMonthView;

class SchedDateTime extends JXDatePicker 
                    implements ChangeListener {

    private SimpleDateFormat  sdf_long;
    private JSpinner          hourSpinner, minSpinner, secSpinner;
    private DateFormat        timeFormat;
    private JPanel            timePanel;
    private SchedGlobalData   mArea;
    private String            mDateStr;

    public SchedDateTime() {
        super();

        getMonthView().setSelectionModel(new SingleDaySelectionModel());
        sdf_long = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setFormats(sdf_long);

        updateUI();
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( getDate() != null ) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(getDate());
                    cal.set(Calendar.HOUR_OF_DAY, ((Integer) hourSpinner.getValue()).intValue());
                    cal.set(Calendar.MINUTE, ((Integer) minSpinner.getValue()).intValue());
                    cal.set(Calendar.SECOND, ((Integer) secSpinner.getValue()).intValue());
                    setDate(cal.getTime());
                }
            }
        } );
    }

    public void initObj(SchedGlobalData  Area,
                        String    DateStr) {
        mArea = Area;
        mDateStr = DateStr;

        if (mDateStr != null) {
            if (mDateStr.trim().length() > 17) {
                try {
                    setDate(sdf_long.parse(mDateStr.trim()));
                }
                catch(ParseException e) {
                    SchedFile.EnterErrorEntry("SchedDateTime"," : Error Converting Date..." + mDateStr);
                    // System.out.println( " Error converting date..." + mDateStr);
                }
            }
        }

        JXMonthView monthView = getMonthView();
        monthView.setBackground(mArea.getScreenColor(39));

        if ( timePanel == null ) {
            createTimePanel();
        }
    }

    private int parseStr(String  mStr) {
        try {
            int i = Integer.parseInt(mStr.trim());
            return i;
        }
        catch(NumberFormatException e) {
            SchedFile.EnterErrorEntry("SchedDateTime"," : Error Converting Time..." + mStr);
            // System.out.println( " Error converting time..." + mStr);
            return 0;
        }
    }

    private void createTimePanel() {
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new FlowLayout());
        newPanel.setBackground(mArea.getScreenColor(40));

        int mHour = 0;
        int mMin = 0;
        int mSec = 0;

        // System.out.println( "Datetime..." + mDateStr + "---");
        if (mDateStr != null) {
            if (mDateStr.trim().length() > 17) {
                mHour = parseStr(mDateStr.substring(11,13));
                mMin = parseStr(mDateStr.substring(14,16));
                mSec = parseStr(mDateStr.substring(17,19));
            }
        };
        SpinnerNumberModel hourModel = new SpinnerNumberModel(mHour,0,24,1);
        SpinnerNumberModel minModel = new SpinnerNumberModel(mMin,0,60,1);
        SpinnerNumberModel secModel = new SpinnerNumberModel(mSec,0,60,1);

        hourSpinner = new JSpinner(hourModel);
        hourSpinner.addChangeListener(this);
        minSpinner = new JSpinner(minModel);
        minSpinner.addChangeListener(this);
        secSpinner = new JSpinner(secModel);
        secSpinner.addChangeListener(this);

        JComponent hourEditor = new JSpinner.NumberEditor(hourSpinner, "00");
        hourSpinner.setEditor(hourEditor);
        JComponent minEditor = new JSpinner.NumberEditor(minSpinner, "00");
        minSpinner.setEditor(minEditor);
        JComponent secEditor = new JSpinner.NumberEditor(secSpinner, "00");
        secSpinner.setEditor(secEditor);

        if( timeFormat == null ) timeFormat = DateFormat.getTimeInstance( DateFormat.SHORT );

        newPanel.add(new JLabel( "Time: hh" ) );
        newPanel.add(hourSpinner);
        newPanel.add(new JLabel( " mm" ) );
        newPanel.add(minSpinner);
        newPanel.add(new JLabel( " ss" ) );
        newPanel.add(secSpinner);

        setLinkPanel(newPanel);
    }

    public String getDateString() {
        if ( getDate() != null ) {
            String strDate = sdf_long.format(getDate());
            return strDate;
        }
        else {
            return "";
        }
    }

    public void setBackground(Color mColor) {
        JFormattedTextField mText = super.getEditor();
        mText.setBackground(mColor);
    }
    public void setForeground(Color mColor) {
        JFormattedTextField mText = super.getEditor();
        mText.setForeground(mColor);
    }

    public void stateChanged(ChangeEvent e) {
        Calendar cal = Calendar.getInstance();
        if (getDate() != null) {
            cal.setTime(getDate());
        }
        cal.set(Calendar.HOUR_OF_DAY, ((Integer) hourSpinner.getValue()).intValue());
        cal.set(Calendar.MINUTE, ((Integer) minSpinner.getValue()).intValue());
        cal.set(Calendar.SECOND, ((Integer) secSpinner.getValue()).intValue());
        setDate(cal.getTime());
    }

}
