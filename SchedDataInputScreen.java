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

class SchedDataInputScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    private JButton              mEnter;
    private JTextArea            mTextArea;
    private SchedGlobalData      mArea;
    private SchedInpScreenArea.PaneObject.TextItem  mTextItem;

    // Dialog constructor.
    public SchedDataInputScreen(JDialog parent, SchedInpScreenArea.PaneObject.TextItem gTextItem)
    {
        super(parent, true);
        mTextItem = gTextItem;

        // Set the characteristics for this dialog instance.
        // setTitle( "Scheduler FE Grid" );

        mArea = SchedFile.getMiscArea();
        setSize( 300, 310 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        this.setLocation(mArea.getFramePointX() + 200, mArea.getFramePointY() + 150);


        mTextArea = new JTextArea(mTextItem.getText(), 10, 20);
        mTextArea.setLineWrap(true);

        JScrollPane mScrollPane = new JScrollPane(mTextArea);

        // Create a panel for the components.
        JPanel dataPane = new JPanel();


        dataPane.setLayout(new SpringLayout());
        dataPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
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
          mTextItem.update_Text( mTextArea.getText() );

          dispose();
      }
    }
}

