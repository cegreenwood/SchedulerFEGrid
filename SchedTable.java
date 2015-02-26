/**
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

import java.io.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.jdesktop.swingx.JXTable;


class SchedTable extends JXTable implements MouseListener {
    public static final long serialVersionUID = 1L;

    private String          mName;
    private boolean         mInFocus;
    private SchedTable      mOtherTable;

    public SchedTable(String mTable) {
        mInFocus = false;
        mName = mTable;
        addMouseListener( this );
    }

    public boolean IsInFocus() {
        return mInFocus;
    }
    public void setInFocus(boolean InFocus) {
        mInFocus = InFocus;
    }
    public void setupOtherTable(SchedTable otherTable) {
        mOtherTable = otherTable;
    }
    public void mousePressed(MouseEvent e) {
        mInFocus = true;
        mOtherTable.setInFocus(false);
        mOtherTable.clearSelection();
        // System.out.println( "Mouse Pressed " + e.getClickCount());
        if (e.getClickCount() == 2) {
            Scheduler mScheduler = SchedGlobalData.getScheduler();
            mScheduler.displayJobDetail();
        }
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mouseClicked(MouseEvent e) {
    }
}

