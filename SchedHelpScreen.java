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

class SchedHelpScreen extends JDialog implements ActionListener 
{
    public static final long serialVersionUID = 1L;

    public  static final String     VERSION  = " Version 7.3.0.0";

    private JButton         mEnter;

    // Instance attributes used in this dialog.
    public Scheduler        parentFrame;
    public SchedGlobalData  mArea;

    // Dialog constructor.
    public SchedHelpScreen(Scheduler parentFrame,
                           SchedGlobalData  Area)
    {
        // Call the parent setting the parent frame and making it modal.
        super( parentFrame, true );

        // Save the owner frame in case we need it later.
        this.parentFrame = parentFrame;
        mArea = Area;

        // Set the characteristics for this dialog instance.
        setTitle( "About Scheduler FE Grid" );
        setSize( 300, 240 );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        // this.setLocation(150,100);
        this.setLocation((int)parentFrame.getLocationOnScreen().getX() + 140,
                         (int)parentFrame.getLocationOnScreen().getY() + 100);


        // Create a panel for the components.
        JPanel helpPane = new JPanel();
        helpPane.setLayout(new SpringLayout());
        helpPane.setBackground( mArea.getBackgroundColor() );

        JLabel label_1 = new JLabel("Blueshire Services Ltd");
        label_1.setFont(new Font("Helvetica", Font.BOLD, 16) );
        helpPane.add(label_1, new SpringLayout.Constraints(Spring.constant(60),
                                                 Spring.constant(30),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));


        JLabel label_2 = new JLabel("     Scheduler FE Grid");
        label_2.setFont(new Font("Helvetica", Font.BOLD, 16) );
        helpPane.add(label_2, new SpringLayout.Constraints(Spring.constant(60),
                                                 Spring.constant(60),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        JLabel label_3 = new JLabel(VERSION);
        label_3.setFont(new Font("Helvetica", Font.BOLD, 14) );
        helpPane.add(label_3, new SpringLayout.Constraints(Spring.constant(90),
                                                 Spring.constant(90),
                                                 Spring.constant(180),
                                                 Spring.constant(20)));

        mEnter = new JButton("  OK  ");
        mEnter.setBackground( mArea.getButtonColor() );
        helpPane.add(mEnter, new SpringLayout.Constraints(Spring.constant(100),
                                                 Spring.constant(160),
                                                 Spring.constant(80),
                                                 Spring.constant(25)));
        mEnter.addActionListener( this );
        mEnter.setActionCommand("Exit");
        mEnter.setMnemonic(KeyEvent.VK_O);

        getContentPane().add( helpPane );

    }

    public void actionPerformed( ActionEvent e ) {
      if ( e.getActionCommand().equals("Exit") ) {
        dispose();
      }
    }
}



