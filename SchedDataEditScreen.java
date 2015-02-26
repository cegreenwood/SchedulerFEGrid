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

class SchedDataEditScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton         mEnter;
    private SchedUpdate     mParentFrame;
    private SchedGlobalData mArea;
    private JTextArea       mTextArea;

    // Instance attributes used in this dialog.
    // public Scheduler        parentFrame;

    // Dialog constructor.
    public SchedDataEditScreen(SchedUpdate parentFrame, SchedGlobalData Area, String editText)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        // Save the owner frame in case we need it later.
        mParentFrame = parentFrame;
        mArea = Area;

        // Set the characteristics for this dialog instance.
        setSize( 300, 320 );

        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        this.setLocation((int)parentFrame.getLocationOnScreen().getX() + 160,
                         (int)parentFrame.getLocationOnScreen().getY() + 100);

        // SchedScreenArea.PaneObject mPane = mDataArea.getPane();

        mTextArea = new JTextArea(editText, 10, 20);
        mTextArea.setLineWrap(true);

        JScrollPane mScrollPane = new JScrollPane(mTextArea);

        // Create a panel for the components.
        JPanel dataPane = new JPanel();

        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        dataPane.setLayout(new SpringLayout());
        dataPane.setBackground(mArea.getBackgroundColor());
        dataPane.add(mScrollPane, new SpringLayout.Constraints(Spring.constant(25),
                                                 Spring.constant(30),
                                                 Spring.constant(240),
                                                 Spring.constant(170)));

        mEnter = new JButton("  OK  ");
        mEnter.setBackground(mArea.getButtonColor());

        dataPane.add(mEnter, new SpringLayout.Constraints(Spring.constant(100),
                                                 Spring.constant(230),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter.addActionListener( this );
        mEnter.setActionCommand("Exit");
        mEnter.setMnemonic(KeyEvent.VK_O);

        getContentPane().add( dataPane );

    }

    public void actionPerformed( ActionEvent e ) {
      if ( e.getActionCommand().equals("Exit") ) {
          mParentFrame.updateText( mTextArea.getText() );
          dispose();
      }
    }
}
