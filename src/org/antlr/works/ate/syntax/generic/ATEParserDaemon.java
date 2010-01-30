package org.antlr.works.ate.syntax.generic;

import org.antlr.works.ate.ATEPanel;

/** */
public class ATEParserDaemon extends Thread {
    final ATESyntaxEngine engine;
    volatile boolean dirty;
    boolean done;
    
    public ATEParserDaemon(ATESyntaxEngine engine) {
        this.engine = engine;
    }
   
    public void setDirty() { this.dirty = true; }

    public void shutDown() { done = true; }
    
    @Override
    public void run() {
        while ( !done ) {
            while ( !dirty ) {
                try { Thread.sleep(1000); }
                catch (InterruptedException ie) { throw new RuntimeException(ie); }
            }
            if ( engine!=null ) engine.process();
        }
    }
}