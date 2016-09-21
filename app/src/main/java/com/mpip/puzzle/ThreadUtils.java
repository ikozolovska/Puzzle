package com.mpip.puzzle;

import android.util.Log;

import java.util.Set;

public class ThreadUtils {

	public static void showThreadNames() { 
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for(Thread t : threadArray) { 
			Log.d("THREAD", "Thread: " + t.getName());
		}
	}
	
	public static void showThreads() { 

        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }

        listThreads(rootGroup, "");
	}
	

    private static void listThreads(ThreadGroup group, String indent) {
        System.out.println(indent + "Group[" + group.getName() + 
                        ":" + group.getClass()+"]");
        int nt = group.activeCount();
        Thread[] threads = new Thread[nt*2 + 10];
        nt = group.enumerate(threads, false);

        for (int i=0; i<nt; i++) {
            Thread t = threads[i];
            
            Log.d("THREAD", indent + "  Thread[" + t.getName() 
                        + ":" + t.getClass() + "]");
        }

        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[ng*2 + 10];
        ng = group.enumerate(groups, false);

        for (int i=0; i<ng; i++) {
            listThreads(groups[i], indent + "  ");
        }
    }
}