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


class threadRunJob implements Runnable {

    private Scheduler             parentFrame;
    private SchedDataArea         mDataArea;
    private String                mJobName;
    private boolean               mCurrentThread;

    threadRunJob(Scheduler      parentFrame,
                 SchedDataArea  dataArea,
                 String         jobName,
                 boolean        currentThread) {
        if ( parentFrame.mDebug == 0) SchedFile.WriteDebugLine("threadRunJob.threadRunJob");

        this.parentFrame = parentFrame;
        mDataArea = dataArea;
        mJobName = jobName;
        mCurrentThread = currentThread;
    }

    public void run() {
        if ( parentFrame.mDebug == 0) SchedFile.WriteDebugLine("threadRunJob.run");

        if ( mDataArea.RunJob( mJobName, mCurrentThread ) != 0 ) {
             parentFrame.errorBox("Error - " + mDataArea.getSysMessage());
         }
    }
}
