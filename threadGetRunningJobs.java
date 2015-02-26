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

class threadGetRunningJobs implements Runnable {
    private SchedGlobalData       mGlobalArea;
    private boolean               mContinue;
    private Scheduler             parentFrame;

    public threadGetRunningJobs(Scheduler        parentFrame,
                                SchedGlobalData  globalArea) {
        if ( parentFrame.mDebug == 0) SchedFile.WriteDebugLine("threadGetRunningJobs.threadGetRunningJobs");

        this.parentFrame = parentFrame;
        mGlobalArea = globalArea;
        mContinue = true;
    }

    public void stopJob() {
        if ( parentFrame.mDebug == 0) SchedFile.WriteDebugLine("threadGetRunningJobs.stopJob");
        mContinue = false;
    }

    public void run() {
        if ( parentFrame.mDebug == 0) SchedFile.WriteDebugLine("threadGetRunningJobs.run");

        int mCount = 0;
        while (mContinue) {
            mCount = mCount + 1;

            parentFrame.saveRunData();
            mGlobalArea.clearJobsRunningVector();
            mGlobalArea.GetJobsRunningData();

            parentFrame.saveChainData();
            mGlobalArea.clearChainsRunningVector();
            mGlobalArea.GetChainsRunningData();

            parentFrame.createRunData1();

            //Sleeps for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch ( InterruptedException e ) {
                mContinue = false;
            }

            mGlobalArea.clearJobsRunningVector();
        }
    }
}

