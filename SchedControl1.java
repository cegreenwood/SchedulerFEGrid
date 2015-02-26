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

import java.awt.event.*;

class SchedControl1 implements ActionListener 
{
    public void actionPerformed( ActionEvent event ) {
        SchedScreenArea.PaneObject mPane = SchedGlobalData.getPane();

        SchedScreenArea.PaneObject.TextItem m_T1 = 
                    mPane.getTextObj(Integer.parseInt(event.getActionCommand()));

        SchedGlobalData.setText( m_T1.getText() );

        // System.out.println(" Button Pressed " + event.getActionCommand() + "--" + m_T1.get_Text() );
        // System.out.println(" Point 2 ");

        Scheduler parentFrame = SchedGlobalData.getScheduler();

        if (m_T1.get_RowType() == 5) {

            SchedEventScreen eventDialog = new SchedEventScreen(parentFrame, m_T1);

            eventDialog.setVisible( true );
        }
        else {

            SchedDataScreen dataDialog = new SchedDataScreen(parentFrame);

            dataDialog.setVisible( true );
        }

    }
}