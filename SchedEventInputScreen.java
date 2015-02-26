/**
 * Written by Colin Greenwood
 * Date June 2008
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

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;

import java.awt.*;

class SchedEventInputScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton               mEnter;
    private JCheckBox[]           mCheckBox;
    private int[]                 mArrayValue;
    private SchedGlobalData       mArea;
    private int                   arrayCount;
    private int                   mComboId;
    private SchedGlobalData.screenPopup  mScreenPopup;

    // Instance attributes used in this dialog.
    private Dialog                mParentDialog;
    private SchedInpScreenArea.PaneObject.TextItem  mTextItem;

    // Dialog constructor.
    public SchedEventInputScreen(Dialog  parentDialog,
                                SchedInpScreenArea.PaneObject.TextItem gTextItem)
    {
        // Set the characteristics for this dialog instance.
        // Call the parent setting the parent frame and making it modal.
        super( parentDialog, true );

        mParentDialog = parentDialog;
        mTextItem = gTextItem;

        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        mArea = SchedFile.getMiscArea();
        mComboId = mTextItem.get_ComboId();
        mScreenPopup = mArea.getPopupObj(mComboId);

        String[] optionString = new String[mArea.getMaxItemId(mComboId)];
        int mCount = 0;
        int mPoint = 0;
        boolean mContinue = true;
        String mText = mTextItem.get_Text();
        while (mContinue) {

            if (mText.length() == 0) {
                mContinue = false;
            }
            else {

                if (mText.indexOf(",", mPoint) == -1) {
                    optionString[mCount] = mText.substring(mPoint);
                    mContinue = false;
                }
                else {
                    optionString[mCount] = mText.substring(mPoint, mText.indexOf(",", mPoint));
                    mCount = mCount + 1;
                    mPoint = mText.indexOf(",", mPoint) + 1;
                }
            }
        }

        // Create a panel for the components.
        JPanel dataPane = new JPanel();
        // dataPane.setBorder(BorderFactory.createLineBorder(Color.black));
        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        dataPane.setLayout(new SpringLayout());
        dataPane.setBackground(mArea.getScreenColor(42));

        arrayCount = mArea.countComboObj(mComboId);
        mCheckBox = new JCheckBox[arrayCount];
        mArrayValue = new int[arrayCount];

        mPoint = 0;

        for (int i1 = 0; i1 < mArea.countComboObj(mComboId); i1++) {
            SchedGlobalData.screenCombo mScreenCombo = mArea.getComboObj(mComboId, i1 + 1);

            mArrayValue[i1] = mScreenCombo.getItemValue();

            mCheckBox[i1] = new JCheckBox(mScreenCombo.getColumnText());
            mCheckBox[i1].setBackground(mArea.getScreenColor(42));
            mCheckBox[i1].setForeground(mArea.getScreenColor(43));

            for (int i2 = 0; i2 <= mCount; i2++) {
                if (mScreenCombo.getColumnText().equals(optionString[i2])) {
                    mCheckBox[i1].setSelected(true);
                    break;
                }
            }

            mPoint = (i1 + 1) * 30;
            dataPane.add(mCheckBox[i1], new SpringLayout.Constraints(Spring.constant(60),
                                                 Spring.constant(mPoint),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));
        }

        setSize( 280, mPoint + 140 );
        this.setLocation(200, 40);

        mEnter = new JButton("  OK  ");
        mEnter.setBackground(mArea.getButtonColor());

        dataPane.add(mEnter, new SpringLayout.Constraints(Spring.constant(100),
                                                 Spring.constant(mPoint + 50),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter.addActionListener( this );
        mEnter.setActionCommand("Exit");
        mEnter.setMnemonic(KeyEvent.VK_O);

        getContentPane().add( dataPane );

    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getActionCommand().equals("Exit") ) {

            int mValue = 0;
            boolean mFirst = true;
            String mReturnString = "";

            for (int i1 = 0; i1 < mArea.countComboObj(mComboId); i1++) {
                if (mCheckBox[i1].isSelected()) {
                    mValue = mValue + mArrayValue[i1];
                    if (mFirst) {
                        mReturnString = mCheckBox[i1].getText();
                        mFirst = false;
                    }
                    else {
                        mReturnString = mReturnString + "," + mCheckBox[i1].getText();
                    }
                }
            }

            mTextItem.update_Text( mReturnString );

            dispose();
        }
    }
}

