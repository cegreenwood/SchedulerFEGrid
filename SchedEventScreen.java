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

class SchedEventScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton             mEnter;
    private SchedGlobalData     mArea;
    private JLabel[]            mLabel;
    private JLabel[]            mLabelText;


    // Dialog constructor.
    public SchedEventScreen(Scheduler           parentFrame,
                            SchedScreenArea.PaneObject.TextItem mTextItem)
    {
        super( parentFrame, true );

        // Set the characteristics for this dialog instance.
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        mArea = SchedFile.getMiscArea();
        int mComboId = mTextItem.get_ComboId();

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


        mLabel = new JLabel[mArea.countComboObj(mComboId)];
        mLabelText = new JLabel[mArea.countComboObj(mComboId)];

        ImageIcon mIcon1 = new ImageIcon("Icons/IconCheckBox1.jpg");
        ImageIcon mIcon2 = new ImageIcon("Icons/IconCheckBox2.jpg");

        mPoint = 0;

        for (int i1 = 0; i1 < mArea.countComboObj(mComboId); i1++) {
            SchedGlobalData.screenCombo mScreenCombo = mArea.getComboObj(mComboId, i1 + 1);

            boolean mSelected = false;
            for (int i2 = 0; i2 <= mCount; i2++) {
                if (mScreenCombo.getColumnText().equals(optionString[i2])) {
                    mSelected = true;
                    break;
                }
            }

            if (mSelected) mLabel[i1] = new JLabel(mIcon2);
            else           mLabel[i1] = new JLabel(mIcon1);

            mLabelText[i1] = new JLabel(mScreenCombo.getColumnText());

            mPoint = (i1 + 1) * 30;
            dataPane.add(mLabel[i1], new SpringLayout.Constraints(Spring.constant(40),
                                                 Spring.constant(mPoint),
                                                 Spring.constant(20),
                                                 Spring.constant(20)));

            dataPane.add(mLabelText[i1], new SpringLayout.Constraints(Spring.constant(66),
                                                 Spring.constant(mPoint),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));
        }

        setSize( 280, mPoint + 140 );

        this.setLocation(SchedFile.getXLocation() + 200,
                         SchedFile.getYLocation() + 40);

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

        dispose();
      }
    }
}

