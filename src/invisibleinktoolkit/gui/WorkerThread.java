/*
 *    Digital Invisible Ink Toolkit
 *    Copyright (C) 2006  K. Hempstalk	
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package invisibleinktoolkit.gui;

import javax.swing.SwingUtilities;

/**
 * A class containing a separate thread that will do the work
 * defined in the overridden construct() method.
 */
public abstract class WorkerThread {
	
	//INNER CLASS

    /** 
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class AThread {
    	
    	/**
    	 * The actual thread.
    	 */
        private Thread mThread;
        
        /**
         * Creates a new synchronized thread with a given thread.
         * 
         * @param t The thread.
         */
        public AThread(Thread t) {
        	mThread = t;
        }
        
        /**        
         * Gets the thread in a synchronized fashion.
         * 
         * @return The thread.
         */
        public synchronized Thread get() {
        	return mThread;
        }
        
        /**
         * Clears the thread in a synchronized fashion.
         *
         */
        public synchronized void clear() { 
        	mThread = null;
        }
    }//end inner class.
    
    
    //VARIABLES
    
    /**
     * Reference to the worker thread running in parallel.
     */
    private AThread mThread;    
           
    //CONSTRUCTORS

    /**
     * Start a thread that will call the construct() method
     * and then exit.
     */
    public WorkerThread() {
        final Runnable doFinished = new Runnable() {
           public void run() { finished(); }
        };

        Runnable doSomeWork = new Runnable() { 
            public void run() {
                try {
                    doWork();
                }
                finally {
                    mThread.clear();
                }
                SwingUtilities.invokeLater(doFinished);
            }
        };

        Thread t = new Thread(doSomeWork);
        mThread = new AThread(t);
    }
    
    //FUNCTIONS

    /**
     * Start the worker thread.
     */
    public void start() {
        Thread t = mThread.get();
        if (t != null) {
            t.start();
        }
    }
    
    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt(){
        Thread t = mThread.get();
        if (t != null) {
            t.interrupt();            
            isInterrupted();
        }
        mThread.clear();
    }
    
    /** 
     * Does some work. 
     */
    public abstract void doWork();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the construct() method has returned.
     */
    public void finished() {
    }
    
    /**
     * Called when the thread is interrupted and stops processing.
     */
    public void isInterrupted(){    	
    }
}
//end of class
